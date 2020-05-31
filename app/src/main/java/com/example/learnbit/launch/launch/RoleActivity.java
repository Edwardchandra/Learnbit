package com.example.learnbit.launch.launch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.model.userdata.teacher.Teacher;
import com.example.learnbit.launch.model.userdata.User;
import com.example.learnbit.launch.onboard.student.StudentOnboardActivity;
import com.example.learnbit.launch.teacher.TeacherMainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class RoleActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout studentButton, teacherButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String name, email;
    private double rating = 5.0;
    private long balance = 200000;

    private static final String detailPreference = "LOGIN_PREFERENCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);

        studentButton = findViewById(R.id.role_StudentButton);
        teacherButton = findViewById(R.id.role_TeacherButton);

        studentButton.setOnClickListener(this);
        teacherButton.setOnClickListener(this);

        getIntentData();
        setupFirebaseAuth();
    }

    private void setupFirebaseAuth(){
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    private void setupFirebaseDatabase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String deviceToken = instanceIdResult.getToken();

            databaseReference.child(user.getUid()).setValue(new User(name, email, deviceToken));
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to save value.", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupFirebaseStorage(){
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("Users").child(user.getUid()).child("profileimage");
    }

    private void uploadProfileImage(){
        Drawable drawable = getResources().getDrawable(R.drawable.teacher_role);

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to upload profile image", Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(taskSnapshot -> Toast.makeText(getApplicationContext(), "Successfully upload profile image", Toast.LENGTH_SHORT).show());
    }

    private void getIntentData(){
        name = getIntent().getStringExtra("profilename");
        email = getIntent().getStringExtra("profileemail");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.role_StudentButton:
                setupFirebaseDatabase();
                setupFirebaseStorage();
                uploadProfileImage();
                savePreferenceData("student");
                Intent studentIntent = new Intent(getApplicationContext(), StudentOnboardActivity.class);
                startActivity(studentIntent);
                break;
            case R.id.role_TeacherButton:
                setupFirebaseDatabase();
                setupFirebaseStorage();
                uploadProfileImage();
                savePreferenceData("teacher");
                databaseReference.child(user.getUid()).child("teacher").setValue(new Teacher(balance, "", rating));
                Intent teacherIntent = new Intent(getApplicationContext(), TeacherMainActivity.class);
                startActivity(teacherIntent);
                break;
            default:
                Toast.makeText(this, "nothing happened", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePreferenceData(String role){
        if (getIntent()!=null){
            SharedPreferences preferences = getSharedPreferences(detailPreference, MODE_PRIVATE);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("role", role);
            editor.apply();
        }
    }
}
