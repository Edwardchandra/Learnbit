package com.example.learnbit.launch.launch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.model.userdata.teacher.Teacher;
import com.example.learnbit.launch.student.StudentMainActivity;
import com.example.learnbit.launch.teacher.TeacherMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RoleActivity extends AppCompatActivity implements View.OnClickListener {

    //Initiate firebase variable
    //Database Reference is used to get the path to the item from the Firebase Database
    private DatabaseReference databaseReference;

    //Preference key used to retrieve preference data(if there's any)
    private static final String detailPreference = "LOGIN_PREFERENCE";
    private static final String preferenceKey = "role";

    //onCreate method is executed when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);

        //initiate elements in layout file
        LinearLayout studentButton = findViewById(R.id.role_StudentButton);
        LinearLayout teacherButton = findViewById(R.id.role_TeacherButton);

        //set element action when it is clicked
        studentButton.setOnClickListener(this);
        teacherButton.setOnClickListener(this);

        setupFirebase();
    }

    //setting up Firebase Authentication, Firebase Database, and Firebase Storage
    //user - get the current signed in user from Firebase Authentication
    //database reference - assign a specific path where data is getting stored/is going to be stored
    private void setupFirebase(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        if (user!=null){
            databaseReference = firebaseDatabase.getReference("Users").child(user.getUid()).child("teacher");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.role_StudentButton:
                studentRoleAction();
                break;
            case R.id.role_TeacherButton:
                teacherRoleAction();
                break;
        }
    }

    //save chosen role data to shared preference
    //role is used to choose which home will be shown(student/teacher)
    private void savePreferenceData(String role){
        if (getIntent()!=null){
            SharedPreferences preferences = getSharedPreferences(detailPreference, MODE_PRIVATE);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(preferenceKey, role);
            editor.apply();
        }
    }

    //action taken when choose student role
    private void studentRoleAction(){
        savePreferenceData("student");
        Intent intent = new Intent(this, StudentMainActivity.class);
        startActivity(intent);
    }

    //action taken when choose teacher role
    private void teacherRoleAction(){
        savePreferenceData("teacher");
        retrieveData();

        Intent intent = new Intent(this, TeacherMainActivity.class);
        startActivity(intent);
    }

    //retrieve data to check if user already signed in before
    private void retrieveData(){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Teacher teacher = dataSnapshot.getValue(Teacher.class);

                if (teacher==null){
                    saveData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });
    }

    //save data with value
    private void saveData(){
        databaseReference.setValue(new Teacher(0, "", 0.0));
    }

    //method to show toast
    private void toast(String string){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }
}
