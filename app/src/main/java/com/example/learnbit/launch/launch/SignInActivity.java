package com.example.learnbit.launch.launch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.learnbit.R;
import com.example.learnbit.launch.extension.BaseActivity;
import com.example.learnbit.launch.extension.SinchService;
import com.example.learnbit.launch.model.userdata.User;
import com.example.learnbit.launch.student.StudentMainActivity;
import com.example.learnbit.launch.teacher.TeacherMainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sinch.android.rtc.SinchError;

public class SignInActivity extends BaseActivity implements SinchService.StartFailedListener, View.OnClickListener {

    //initiate elements variable to connect to layout view
    private androidx.appcompat.widget.Toolbar signInToolbar;
    private Button signInButton, googleSignInButton;
    private EditText emailET;
    private EditText passwordET;

    //initiate Firebase variable
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    //initiate preference key to retrieve stored preference data
    private static final String detailPreference = "LOGIN_PREFERENCE";
    private static final String preferenceKey = "role";

    //retrieved preference data is being stored in this variable
    private String role;

    //google sign in
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    //onCreate method is executed when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //connect elements variable with layout view elements
        signInToolbar = findViewById(R.id.signInToolbar);
        signInButton = findViewById(R.id.signIn_SignInButton);
        googleSignInButton = findViewById(R.id.signIn_GoogleSignInButton);
        emailET = findViewById(R.id.signIn_EmailET);
        passwordET = findViewById(R.id.signIn_PasswordET);
        Button forgotPasswordButton = findViewById(R.id.forgotPasswordButton);

        //set sign in button to disabled
        //enabled when sinch service is connected
        signInButton.setEnabled(false);
        googleSignInButton.setEnabled(false);

        //set onClick listener to the onClick method
        forgotPasswordButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        googleSignInButton.setOnClickListener(this);

        setupToobar();
        setupFirebase();
        getPreferenceData();
        setupGoogleSignIn();
    }

    private void setupGoogleSignIn(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    //setting up custom toolbar for the view
    private void setupToobar(){
        setSupportActionBar(signInToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    //setting up firebase
    //initiate firebase auth instance
    private void setupFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
    }

    //toolbar button elements is being executed here
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    //method to check if edittext is empty or not
    private boolean isEmpty(EditText editText){
        return TextUtils.isEmpty(editText.getText().toString());
    }

    //method to check if certain edittext contained a valid email
    private boolean isValidEmail(EditText editText) {
        return !TextUtils.isEmpty(editText.getText().toString()) && android.util.Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches();
    }

    //method when edittext condition is fulfilled
    private void signIn(){
        firebaseAuth.signInWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        user = firebaseAuth.getCurrentUser();
                        if (user!=null) {
                            savePreferenceData("email");
                            startSinchClient();
                        }
                    }else{
                        toast(getString(R.string.failed_signin));
                    }
                });
    }

    private void signInWithGoogle(){
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account!=null){
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                toast(e.toString());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        user = firebaseAuth.getCurrentUser();
                        if (user!=null){
                            savePreferenceData("google");
                            startSinchClient();
                            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
                                String deviceToken = instanceIdResult.getToken();
                                FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).setValue(new User(user.getDisplayName(), user.getEmail(), deviceToken));
                            }).addOnFailureListener(e -> toast(getString(R.string.save_failed)));
                        }
                    } else {
                        toast(getString(R.string.failed_signin));
                    }
                });
    }

    private void updateUI(FirebaseUser user){
        if (user!=null){
            if (role.equals("student")){
                Intent intent = new Intent(this, StudentMainActivity.class);
                startActivity(intent);
            }else if (role.equals("teacher")){
                Intent intent = new Intent(this, TeacherMainActivity.class);
                startActivity(intent);
            }else{
                Intent intent = new Intent(this, RoleActivity.class);
                startActivity(intent);
            }
        }
    }

    //retrieve stored preference data from Shared Preference
    private void getPreferenceData(){
        String defaultValue = "";

        if (getApplicationContext()!=null){
            SharedPreferences preferences = getApplicationContext().getSharedPreferences(detailPreference, Context.MODE_PRIVATE);
            role = preferences.getString(preferenceKey, defaultValue);
        }
    }

    //if sinch service is connected, set sign in button to available and start listening to sinch service
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        signInButton.setEnabled(true);
        googleSignInButton.setEnabled(true);
        getSinchServiceInterface().setStartListener(this);
    }

    //if sinch service failed to start, show error
    @Override
    public void onStartFailed(SinchError error) {
        toast(error.toString());
    }

    //if sinch service is started, execute UPDATE UI
    @Override
    public void onStarted() {
        updateUI(user);
    }

    //check if sinch service is null/started
    //if sinch service is null, initiate sinch service
    //if sinch service is already initiated, execute UPDATE UI
    private void startSinchClient() {
        if (!getSinchServiceInterface().isStarted()){
            if (user!=null){
                getSinchServiceInterface().startClient(user.getUid());
            }
        }else{
            updateUI(user);
        }
    }

    //OnClick method is executed when an OnClick assigned element is being clicked.
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signIn_SignInButton:
                checkEditText();
                break;
            case R.id.signIn_GoogleSignInButton:
                signInWithGoogle();
                break;
            case R.id.forgotPasswordButton:
                Intent intent = new Intent(this, ForgotPasswordActivity.class);
                startActivity(intent);
                break;
        }
    }

    //check if edittext fulfilled the conditions
    //if the conditions is fulfilled, then execute SIGN IN METHOD
    //if conditions is not fulfilled, then the corresponding edittext will display error
    private void checkEditText(){
        if(isEmpty(emailET)){
            emailET.setError(getString(R.string.email_field_error));
        }else if(!isValidEmail(emailET)){
            emailET.setError(getString(R.string.email_field_error_format));
        }else if(isEmpty(passwordET)){
            passwordET.setError(getString(R.string.password_field_error));
        }else if(passwordET.getText().toString().length() < 7){
            passwordET.setError(getString(R.string.password_field_error_character));
        }else{
            signIn();
        }
    }

    private void savePreferenceData(String signInType){
        if (getIntent()!=null){
            SharedPreferences preferences = getSharedPreferences("SIGN_IN_PREFERENCE", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("signInType", signInType);
            editor.apply();
        }
    }

    //method to show toast
    private void toast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}

