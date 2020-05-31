package com.example.learnbit.launch.student.profile.accountsettings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.learnbit.BuildConfig;
import com.example.learnbit.R;
import com.example.learnbit.launch.model.userdata.User;
import com.example.learnbit.launch.model.userdata.teacher.Teacher;
import com.example.learnbit.launch.student.StudentMainActivity;
import com.example.learnbit.launch.teacher.TeacherMainActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StudentEditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView profileImageView;
    private EditText profileName, profileEmail, profilePassword;

    private String cameraFilePath;

    private Intent galleryIntent;
    private Intent cameraIntent;

    private FirebaseUser user;

    private DatabaseReference databaseReference;

    private StorageReference storageReference;

    private User users = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_edit_profile);

        profileImageView = findViewById(R.id.editProfile_ImageView);
        LinearLayout changeProfileImageButton = findViewById(R.id.editProfile_ChangeImageButton);
        Button saveChangesButton = findViewById(R.id.editProfile_SaveButton);
        profileName = findViewById(R.id.editProfile_NameET);
        profileEmail = findViewById(R.id.editProfile_EmailET);
        profilePassword = findViewById(R.id.editProfile_PasswordET);

        changeProfileImageButton.setOnClickListener(this);
        saveChangesButton.setOnClickListener(this);

        toolbarSetup();
        setupFirebaseAuth();
        setupFirebaseDatabase();
        setupFirebaseStorage();
        retrieveDataFromFirebase();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        cameraFilePath = "file://" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case 0:
                    Uri selectedImage = data.getData();
                    profileImageView.setImageURI(selectedImage);
                    break;
                case 1:
                    profileImageView.setImageURI(Uri.parse(cameraFilePath));
                    break;
            }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void pickFromGallery() {
        galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(galleryIntent, 0);
    }

    private void captureFromCamera() {
        try {
            cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", createImageFile()));
            startActivityForResult(cameraIntent, 1);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editProfile_ChangeImageButton:
                pickFromGallery();
                break;
            case R.id.editProfile_SaveButton:
                if (profilePassword.getText().toString().isEmpty()){
                    profilePassword.setError("Password shouldn't be empty");
                }else{
                    updateProfileData();
                    uploadProfileImage();
                    Intent intent = new Intent(getApplicationContext(), StudentMainActivity.class);
                    startActivity(intent);
                }
                break;
            default:
                Toast.makeText(this, "nothing happened", Toast.LENGTH_SHORT).show();
        }
    }

    private void toolbarSetup(){
        if (getSupportActionBar() != null){
            setTitle("Edit Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupFirebaseAuth(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    private void setupFirebaseDatabase(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference("Users").child(user.getUid());
    }

    private void setupFirebaseStorage(){
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("Users").child(user.getUid()).child("profileimage");
    }

    private void updateProfileData(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String token = instanceIdResult.getToken();
            databaseReference.setValue(new User(profileName.getText().toString(), profileEmail.getText().toString(), token));

            if (user.getEmail()!=null){
                AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), profilePassword.getText().toString());
                user.reauthenticate(authCredential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        user.updateEmail(profileEmail.getText().toString());
                        Toast.makeText(getApplicationContext(), "Your profile data successfully updated", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Your profile data failed to update", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "failed to save data", Toast.LENGTH_SHORT).show();
        });
    }

    private void retrieveDataFromFirebase(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users = dataSnapshot.getValue(User.class);
                updateUI(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "failed to retrieve profile data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(FirebaseUser user){
        if (user!=null){
            profileName.setText(users.getName());
            profileEmail.setText(users.getEmail());
        }
    }

    private void uploadProfileImage(){
        Bitmap bitmap = ((BitmapDrawable) profileImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to upload profile image", Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(taskSnapshot -> Toast.makeText(getApplicationContext(), "Successfully upload profile image", Toast.LENGTH_SHORT).show());
    }
}
