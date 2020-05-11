package com.example.learnbit.launch.launch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.TeacherMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar signInToolbar;
    private Button signInButton;
    private Button signInWithGoogleButton;
    private Button signInWithFacebookButton;
    private EditText emailET;
    private EditText passwordET;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        signInToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.signInToolbar);
        signInButton = (Button) findViewById(R.id.signIn_SignInButton);
        signInWithGoogleButton = (Button) findViewById(R.id.signIn_googleSignInButton);
        signInWithFacebookButton = (Button) findViewById(R.id.signIn_facebookSignInButton);
        emailET = (EditText) findViewById(R.id.signIn_EmailET);
        passwordET = (EditText) findViewById(R.id.signIn_PasswordET);


        setSupportActionBar(signInToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmpty(emailET)){
                    emailET.setError("Email must not be empty");
                }else if(!isValidEmail(emailET)){
                    emailET.setError("Email must be in valid format");
                }else if(isEmpty(passwordET)){
                    passwordET.setError("Password shouldn't be empty.");
                }else if(passwordET.getText().toString().length() <= 6){
                    passwordET.setError("Password must be more than 6 characters");
                }else{
                    signIn();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isEmpty(EditText editText){
        return TextUtils.isEmpty(editText.getText().toString());
    }

    private boolean isValidEmail(EditText editText) {
        return !TextUtils.isEmpty(editText.getText().toString()) && android.util.Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches();
    }

    private void signIn(){
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                        }else{
                            Toast.makeText(SignInActivity.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user){
        if (user!=null){
            Intent intent = new Intent(this, TeacherMainActivity.class);
            startActivity(intent);
        }
    }
}
