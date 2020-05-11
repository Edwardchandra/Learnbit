package com.example.learnbit.launch.teacher.profile.accountsettings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.TeacherMainActivity;
import com.example.learnbit.launch.teacher.profile.TeacherProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText newPasswordET, oldPasswordET, repeatOldPasswordET;
    private Button saveChangesButton;
    
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        
        oldPasswordET = (EditText) findViewById(R.id.changePassword_OldPassword);
        repeatOldPasswordET = (EditText) findViewById(R.id.changePassword_OldPasswordRepeat);
        newPasswordET = (EditText) findViewById(R.id.changePassword_NewPassword);
        saveChangesButton = (Button) findViewById(R.id.changePassword_SaveButton);

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEditText();
            }
        });

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Change Password");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setupFirebaseAuth();
    }
    
    private void setupFirebaseAuth(){
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    private void changePassword(){
        final String email = user.getEmail();
        final String password = oldPasswordET.getText().toString();

        AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);
        
        user.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    user.updatePassword(newPasswordET.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ChangePasswordActivity.this, "Password changed succesfully", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(ChangePasswordActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(ChangePasswordActivity.this, "Your entered password didn't match your account password", Toast.LENGTH_SHORT).show();
                }
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
            Intent intent = new Intent(this, TeacherMainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}
