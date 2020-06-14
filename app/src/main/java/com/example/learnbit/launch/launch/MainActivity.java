package com.example.learnbit.launch.launch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.extension.BaseActivity;
import com.example.learnbit.launch.extension.SinchService;
import com.example.learnbit.launch.student.StudentMainActivity;
import com.example.learnbit.launch.teacher.TeacherMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sinch.android.rtc.SinchError;

public class MainActivity extends BaseActivity implements View.OnClickListener, SinchService.StartFailedListener {

    //Preference key used to retrieve preference data(if there's any)
    private static final String detailPreference = "LOGIN_PREFERENCE";
    private static final String preferenceKey = "role";

    //Retrieved preference data stored in this variable
    private String role;

    //Initiate firebase variable
    //Firebase user variable used to retrieve logged in user(if there's any)
    //If there's no user logged in, it remains empty/null
    private FirebaseUser user;


    //Method execute when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initiate elements in layout file
        Button launchSignInButton = findViewById(R.id.launchSignInButton);
        Button launchRegisterButton = findViewById(R.id.launchRegisterButton);

        //Set button elements onClick action
        launchSignInButton.setOnClickListener(this);
        launchRegisterButton.setOnClickListener(this);

        getPreferenceData();
        setupFirebase();
    }

    //Method execute when there's an onclick action in one of the view element
    //break is used to finish the action if when the certain case is meet, it finish its execution without jumping to another case
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.launchSignInButton:
                signInAction();
                break;
            case R.id.launchRegisterButton:
                registerAction();
                break;
        }
    }

    //Action method when sign in button is clicked
    private void signInAction(){
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    //Action method when register button is clicked
    private void registerAction(){
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
    }

    //if retrieved preference data equals student value
    private void studentRoleAction(){
        Intent intent = new Intent(this, StudentMainActivity.class);
        startActivity(intent);
    }

    //if retrieved preference data equals teacher value
    private void teacherRoleAction(){
        Intent intent = new Intent(this, TeacherMainActivity.class);
        startActivity(intent);
    }

    //Method to retrieve firebase data
    //Firebase Auth - Firebase Authentication - Where user login/register data is being stored
    //Get Current User - if there's any user already signed in into the application, it returns the user, else it returns null. Current active user is stired into user variable.
    private void setupFirebase(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    //Method to start intent when user is not null, and when role is not null and matched the value
    private void updateUI(FirebaseUser user){
        if (user!=null){
            if (role.equals("student")){
               studentRoleAction();
            }else if (role.equals("teacher")){
                teacherRoleAction();
            }
        }
    }

    //retrieve stored preference data(if there's any)
    private void getPreferenceData(){
        //Default value if there's no retrieved value
        String defaultValue = "";

        if (getApplicationContext()!=null){
            SharedPreferences preferences = getApplicationContext().getSharedPreferences(detailPreference, Context.MODE_PRIVATE);
            role = preferences.getString(preferenceKey, defaultValue);
        }
    }

    //when back button is pressed, return nothing so it exit directly
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //when sinch service is connected, start listening to the service
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        getSinchServiceInterface().setStartListener(this);

        if (user!=null){
            startSinchClient();
        }
    }

    //if sinch service failed to start, return error
    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
    }

    //if sinch service is started, run UPDATE UI METHOD
    @Override
    public void onStarted() {
        updateUI(user);
    }

    //Check if sinch service is already started or not
    //if sinch service is not yet started then start the sinch service with UID from FIREBASE USER
    //if sinch service is started, run UPDATE UI METHOD
    private void startSinchClient() {
        if (!getSinchServiceInterface().isStarted()){
            getSinchServiceInterface().startClient(user.getUid());
        }else{
            updateUI(user);
        }
    }
}
