package com.example.learnbit.launch.reusableactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.student.StudentMainActivity;
import com.example.learnbit.launch.teacher.TeacherMainActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText newPasswordET, oldPasswordET, repeatOldPasswordET;
    private Button saveChangesButton;
    
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private static final String detailPreference = "LOGIN_PREFERENCE";
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        
        oldPasswordET = findViewById(R.id.changePassword_OldPassword);
        repeatOldPasswordET = findViewById(R.id.changePassword_OldPasswordRepeat);
        newPasswordET = findViewById(R.id.changePassword_NewPassword);
        saveChangesButton = findViewById(R.id.changePassword_SaveButton);

        saveChangesButton.setOnClickListener(v -> checkEditText());

        setupToolbar();
        setupFirebaseAuth();
    }

    private void setupToolbar(){
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Change Password");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }
    
    private void setupFirebaseAuth(){
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    private void changePassword(){
        final String email = user.getEmail();
        final String password = oldPasswordET.getText().toString();

        AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);
        
        user.reauthenticate(authCredential).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                user.updatePassword(newPasswordET.getText().toString()).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        Toast.makeText(ChangePasswordActivity.this, "Password changed succesfully", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ChangePasswordActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(ChangePasswordActivity.this, "Your entered password didn't match your account password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkEditText(){
        if (oldPasswordET.getText().toString().isEmpty()){
            oldPasswordET.setError("Old password shouldn't be empty");
        }else if (oldPasswordET.getText().toString().length() <= 6){
            oldPasswordET.setError("Old password must be more than 6 characters");
        }else if (repeatOldPasswordET.getText().toString().isEmpty()){
            repeatOldPasswordET.setError("Repeat old password shouldn't be empty");
        }else if (!repeatOldPasswordET.getText().toString().equals(oldPasswordET.getText().toString())){
            repeatOldPasswordET.setError("Repeat old password should have be the same with old password");
        }else if (newPasswordET.getText().toString().isEmpty()){
            newPasswordET.setError("New password shouldn't be empty");
        }else{
            changePassword();
            getPreferenceData();
            if (role.equals("student")){
                Intent intent = new Intent(this, StudentMainActivity.class);
                startActivity(intent);
            }else if (role.equals("teacher")){
                Intent intent = new Intent(this, TeacherMainActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    private void getPreferenceData(){
        if (getApplicationContext()!=null){
            SharedPreferences preferences = getApplicationContext().getSharedPreferences(detailPreference, Context.MODE_PRIVATE);
            role = preferences.getString("role", "");
        }
    }
}
