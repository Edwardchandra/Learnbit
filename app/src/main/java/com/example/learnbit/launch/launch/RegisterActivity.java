package com.example.learnbit.launch.launch;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.extension.BaseActivity;
import com.example.learnbit.launch.extension.SinchService;
import com.example.learnbit.launch.model.userdata.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sinch.android.rtc.SinchError;

import java.io.ByteArrayOutputStream;

public class RegisterActivity extends BaseActivity implements View.OnClickListener, SinchService.StartFailedListener {

    //Initiate elements from Layout file
    private androidx.appcompat.widget.Toolbar registerToolbar;
    private EditText nameET, emailET, passwordET;
    private Button createAccountButton;

    //Initiate firebase variable
    //Firebase auth variable used to setting up firebase auth instance;
    //Firebase user variable used to retrieve logged in user(if there's any)
    //If there's no user logged in, it remains empty/null
    //Database Reference is used to get the path to the item from the Firebase Database
    //Storage Reference is used to get the path to the item from the Firebase Storage
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;

    //Method execute when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initiate elements from layout file
        registerToolbar = findViewById(R.id.registerToolbar);
        createAccountButton = findViewById(R.id.register_CreateAccountButton);
        Button signInButton = findViewById(R.id.register_SignInButton);
        nameET = findViewById(R.id.register_NameET);
        emailET = findViewById(R.id.register_EmailET);
        passwordET = findViewById(R.id.register_PasswordET);

        //set register button to be disabled until sinch service is connected
        createAccountButton.setEnabled(false);

        //set button elements onClick action
        signInButton.setOnClickListener(this);
        createAccountButton.setOnClickListener(this);

        setupToolbar();
        setupFirebase();
        checkRegisterButton();
    }

    //Check required edittext before proceed with the action
    //if edittext fulfill requirements needed, execute action
    //if not, then edittext will give an error
    private void checkEditText(){
        if(isEmpty(nameET)){
            nameET.setError(getString(R.string.name_field_error));
        }else if(nameET.getText().toString().length() < 4){
            nameET.setError(getString(R.string.name_field_error_character));
        }else if(isEmpty(emailET)){
            emailET.setError(getString(R.string.email_field_error));
        }else if(!isValidEmail(emailET)){
            emailET.setError(getString(R.string.email_field_error_format));
        }else if(isEmpty(passwordET)){
            passwordET.setError(getString(R.string.password_field_error));
        }else if(passwordET.getText().toString().length() < 7){
            passwordET.setError(getString(R.string.password_field_error_character));
        }else{
            createAccount();
        }
    }

    //setting up custom toolbar for activity
    private void setupToolbar(){
        setSupportActionBar(registerToolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    //Actions for menus in toolbar
    //When menu in toolbar is selected, action is being executed from this method
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //edittext empty/not checker method
    private boolean isEmpty(EditText editText){
        return TextUtils.isEmpty(editText.getText().toString());
    }

    //check email address in edittext is valid or not
    private boolean isValidEmail(EditText editText) {
        return !TextUtils.isEmpty(editText.getText().toString()) && android.util.Patterns.EMAIL_ADDRESS.matcher(editText.getText().toString()).matches();
    }

    //setting up firebase
    private void setupFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("Users");
    }

    //OnClick method is executed when an OnClick assigned element is being clicked
    //break is use as breaking point where when certain case is matched, it doesn't check for other cases anymore.
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_CreateAccountButton:
                checkEditText();
                break;
            case R.id.register_SignInButton:
                signInAction();
                break;
            }
    }

    //when sign in button is clicked, launch intent action to sign in activity
    private void signInAction(){
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    //create account in Firebase Auth
    //if task is successfull, set user to current registered user, save user information to database and storage, and update ui
    //if task is not successfull, return toast with error
    private void createAccount(){
        firebaseAuth.createUserWithEmailAndPassword(emailET.getText().toString(), passwordET.getText().toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        user = firebaseAuth.getCurrentUser();
                        if (user!=null){
                            saveData();
                            uploadProfileImage();
                            startSinchClient();
                        }
                    }else{
                        toast(getString(R.string.failed_registration));
                    }
                }).addOnCanceledListener(() -> toast(getString(R.string.failed_registration_cancelled)));
    }

    //check if user is null or not
    //if user is not null, launch intent to another activity
    private void updateUI(FirebaseUser user){
        if (user!=null){
            Intent intent = new Intent(this, RoleActivity.class);
            startActivity(intent);
        }
    }

    //toast method to show toast
    private void toast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    //Save general user data to Firebase Database
    //In this method, get Instance Id is used tor retrieve the device token that is currently using the device.
    private void saveData(){
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            String deviceToken = instanceIdResult.getToken();
            savePreferenceData();
            databaseReference.child(user.getUid()).setValue(new User(nameET.getText().toString(), emailET.getText().toString(), deviceToken));
        }).addOnFailureListener(e -> toast(getString(R.string.save_failed)));
    }

    //Save initial user profile image to Firebase Storage
    //in this method, it is going to get the resource from the drawable folder
    //drawable retrieved then is converted to bitmap
    //bitmap is then stored to the firebase
    private void uploadProfileImage(){
        Drawable drawable = getResources().getDrawable(R.drawable.teacher_role);

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageReference.child(user.getUid()).child("profileimage").putBytes(data);
        uploadTask.addOnFailureListener(e -> toast(getString(R.string.upload_failed)))
                .addOnSuccessListener(taskSnapshot -> {});
    }

    //check if register button is enabled/disabled
    private void checkRegisterButton(){
        if (!createAccountButton.isEnabled()){
            createAccountButton.setBackground(getResources().getDrawable(R.drawable.button_disabled));
        }else{
            createAccountButton.setBackground(getResources().getDrawable(R.drawable.second_primary_button));
        }
    }

    //if sinch service is connected, start listening to sinch service and set register button to enabled
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        createAccountButton.setEnabled(true);
        checkRegisterButton();
        getSinchServiceInterface().setStartListener(this);
    }

    //if sinch service failed to start, return an error toast
    @Override
    public void onStartFailed(SinchError error) {
        toast(error.toString());
    }

    //if sinch service is started
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

    private void savePreferenceData(){
        if (getIntent()!=null){
            SharedPreferences preferences = getSharedPreferences("SIGN_IN_PREFERENCE", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("signInType", "email");
            editor.apply();
        }
    }
}
