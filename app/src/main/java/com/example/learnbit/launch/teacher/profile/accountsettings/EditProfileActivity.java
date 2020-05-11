package com.example.learnbit.launch.teacher.profile.accountsettings;

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
import com.example.learnbit.launch.model.userdata.teacher.Teacher;
import com.example.learnbit.launch.model.userdata.User;
import com.example.learnbit.launch.teacher.TeacherMainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView profileImageView;
    private LinearLayout changeProfileImageButton;
    private Button saveChangesButton;
    private EditText profileName, profileEmail, profileBio;

    private String cameraFilePath;

    private Intent galleryIntent;
    private Intent cameraIntent;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference1;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private User users = new User();
    private Teacher teacher = new Teacher();

    private double rating;
    private long balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profileImageView = findViewById(R.id.editProfile_ImageView);
        changeProfileImageButton = findViewById(R.id.editProfile_ChangeImageButton);
        saveChangesButton = findViewById(R.id.editProfile_SaveButton);
        profileName = findViewById(R.id.editProfile_NameET);
        profileEmail = findViewById(R.id.editProfile_EmailET);
        profileBio = findViewById(R.id.editProfile_BioET);

        changeProfileImageButton.setOnClickListener(this);
        saveChangesButton.setOnClickListener(this);

        toolbarSetup();
        setupFirebaseAuth();
        setupFirebaseDatabase();
        setupFirebaseStorage();
        retrieveDataFromFirebase();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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
                updateProfileData();
                uploadProfileImage();
                Intent intent = new Intent(getApplicationContext(), TeacherMainActivity.class);
                startActivity(intent);
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
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    private void setupFirebaseDatabase(){
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference("Users").child(user.getUid());
        databaseReference1 = firebaseDatabase.getReference("Users").child(user.getUid()).child("teacher");
    }

    private void setupFirebaseStorage(){
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("Users").child(user.getUid()).child("profileimage");
    }

    private void updateProfileData(){
        databaseReference.setValue(new User(profileName.getText().toString(), profileEmail.getText().toString()));
        databaseReference1.setValue(new Teacher(balance, profileBio.getText().toString(), rating));

        Toast.makeText(this, "Your profile data successfully updated", Toast.LENGTH_SHORT).show();
    }

    private void retrieveDataFromFirebase(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users = dataSnapshot.getValue(User.class);
                updateUI(user);

                Teacher teacher = dataSnapshot.child("teacher").getValue(Teacher.class);

                if (teacher!=null){
                    if (teacher.getDescription().equals("")){
                        profileBio.setText("This teacher has yet to leave a description.");
                    }else {
                        profileBio.setText(teacher.getDescription());
                    }

                    rating = teacher.getRating();
                    balance = teacher.getBalance();
                }
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
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to upload profile image", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "Successfully upload profile image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
