package com.example.learnbit.launch.launch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.student.StudentMainActivity;
import com.example.learnbit.launch.teacher.TeacherMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String detailPreference = "LOGIN_PREFERENCE";
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button launchSignInButton = findViewById(R.id.launchSignInButton);
        Button launchRegisterButton = findViewById(R.id.launchRegisterButton);

        launchSignInButton.setOnClickListener(this);
        launchRegisterButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.launchSignInButton:
                Intent signInIntent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(signInIntent);
                break;
            case R.id.launchRegisterButton:
                Intent registerIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(registerIntent);
                break;
            default:
                Toast.makeText(this, "nothing happened", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPreferenceData();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        updateUI(user);
    }

    private void updateUI(FirebaseUser user){
        if (user!=null){
            if (role.equals("student")){
                Intent intent = new Intent(this, StudentMainActivity.class);
                startActivity(intent);
            }else if (role.equals("teacher")){
                Intent intent = new Intent(this, TeacherMainActivity.class);
                startActivity(intent);
            }
        }
    }

    private void getPreferenceData(){
        if (getApplicationContext()!=null){
            SharedPreferences preferences = getApplicationContext().getSharedPreferences(detailPreference, Context.MODE_PRIVATE);
            role = preferences.getString("role", "");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //do nothing
    }
}
