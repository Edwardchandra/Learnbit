package com.example.learnbit.launch.student.profile.accountsettings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.learnbit.BuildConfig;
import com.example.learnbit.R;
import com.example.learnbit.launch.model.userdata.User;
import com.example.learnbit.launch.model.userdata.teacher.Teacher;
import com.example.learnbit.launch.student.StudentMainActivity;
import com.example.learnbit.launch.teacher.TeacherMainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private User users = new User();

    private String signInType;

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

        setupToolbar();
        setupFirebase();
        getPreferenceData();
        checkPreferenceData();
        retrieveData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
            if (requestCode == 0) {
                if (data != null) {
                    Uri selectedImage = data.getData();
                    profileImageView.setImageURI(selectedImage);
                }
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
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(galleryIntent, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editProfile_ChangeImageButton:
                pickFromGallery();
                break;
            case R.id.editProfile_SaveButton:
                if (signInType.equalsIgnoreCase("email")){
                    checkEditText();
                }else if (signInType.equalsIgnoreCase("google")){
                    checkEditTextGoogle();
                }
                break;
        }
    }

    private void checkEditText(){
        if (profileName.getText().toString().isEmpty()){
            profileName.setError(getString(R.string.name_field_error));
        }else if (profileName.getText().toString().length() < 3){
            profileName.setError(getString(R.string.name_field_error_character));
        }else if (profileEmail.getText().toString().isEmpty()){
            profileEmail.setError(getString(R.string.email_field_error));
        }else if (!isValidEmail(profileEmail)){
            profileEmail.setError(getString(R.string.email_field_error_format));
        }else if (profilePassword.getText().toString().isEmpty()){
            profilePassword.setError(getString(R.string.password_field_error));
        }else if (profilePassword.getText().toString().length() < 7){
            profilePassword.setError(getString(R.string.password_field_error_character));
        }else{
            updateProfileData();
        }
    }

    private boolean isValidEmail(EditText editText) {
        return !TextUtils.isEmpty(editText.getText().toString()) && android.util.Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches();
    }

    private void setupToolbar(){
        if (getSupportActionBar() != null){
            setTitle("Edit Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupFirebase(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        user = firebaseAuth.getCurrentUser();
        storageReference = firebaseStorage.getReference().child("Users").child(user.getUid()).child("profileimage");
        databaseReference = firebaseDatabase.getReference("Users").child(user.getUid());
    }

    private void updateProfileData(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String token = instanceIdResult.getToken();

            if (user.getEmail()!=null){
                AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), profilePassword.getText().toString());
                user.reauthenticate(authCredential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        databaseReference.setValue(new User(profileName.getText().toString(), profileEmail.getText().toString(), token));
                        user.updateEmail(profileEmail.getText().toString());
                        uploadProfileImage();
                        Intent intent = new Intent(getApplicationContext(), StudentMainActivity.class);
                        startActivity(intent);
                    }else{
                        toast(getString(R.string.save_failed));
                    }
                });
            }
        }).addOnFailureListener(e -> toast(getString(R.string.save_failed)));
    }

    private void retrieveData(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users = dataSnapshot.getValue(User.class);
                updateUI(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(getApplicationContext()).load(uri).into(profileImageView))
                .addOnFailureListener(e -> toast(getString(R.string.retrieve_failed)));
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
        uploadTask.addOnFailureListener(e -> toast(getString(R.string.upload_failed)))
                .addOnSuccessListener(taskSnapshot -> toast(getString(R.string.upload_success)));
    }

    private void getPreferenceData(){
        String defaultValue = "";

        if (getApplicationContext()!=null){
            SharedPreferences preferences = getApplicationContext().getSharedPreferences("SIGN_IN_PREFERENCE", Context.MODE_PRIVATE);
            signInType = preferences.getString("signInType", defaultValue);
        }
    }

    private void checkPreferenceData(){
        if (signInType.equalsIgnoreCase("google")){
            profileEmail.setVisibility(View.GONE);
            profilePassword.setVisibility(View.GONE);
        }else{
            profileEmail.setVisibility(View.VISIBLE);
            profilePassword.setVisibility(View.VISIBLE);
        }
    }

    private void checkEditTextGoogle(){
        if(profileName.getText().toString().isEmpty()){
            profileName.setError(getString(R.string.name_field_error));
        }else if(profileName.getText().toString().length() < 3){
            profileName.setError(getString(R.string.name_field_error_character));
        }else{
            updateProfileDataGoogle();
        }
    }

    private void updateProfileDataGoogle(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String token = instanceIdResult.getToken();

            if (user.getEmail()!=null){
                uploadProfileImage();
                databaseReference.setValue(new User(profileName.getText().toString(), user.getEmail(), token));
                Intent intent = new Intent(getApplicationContext(), TeacherMainActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(e -> toast(getString(R.string.save_failed)));
    }

    //show toast
    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
