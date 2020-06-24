package com.example.learnbit.launch.launch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.learnbit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        setupToolbar();
        email = findViewById(R.id.emailET);
        Button passwordResetButton = findViewById(R.id.passwordResetButton);
        passwordResetButton.setOnClickListener((v) -> {
            if (email.getText().toString().isEmpty()){
                email.setError(getString(R.string.email_field_error));
            }else if (!isValidEmail(email)){
                email.setError(getString(R.string.email_field_error_format));
            }else{
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(this, "Password reset has been sent to your email. Please check your email.", Toast.LENGTH_SHORT).show();
                    }else if (task.isCanceled()){
                        Toast.makeText(this, "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, "Failed to send password reset email. Please check your internet connection or your email.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setupToolbar(){
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Reset Password");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    //method to check if certain edittext contained a valid email
    private boolean isValidEmail(EditText editText) {
        return !TextUtils.isEmpty(editText.getText().toString()) && android.util.Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches();
    }
}