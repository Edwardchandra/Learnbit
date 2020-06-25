package com.example.learnbit.launch.student.course.mycoursedetail.terminate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.learnbit.R;
import com.example.learnbit.launch.student.course.mycoursedetail.terminate.model.Terminate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TerminateActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView terminateReason;
    private String dateTime;
    private long timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminate);

        terminateReason = findViewById(R.id.terminateReason);
        Button terminateRequest = findViewById(R.id.terminateRequest);
        terminateRequest.setOnClickListener(this);
        setupToolbar();
        getCurrentDateTime();
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Terminate Request");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void checkEditText(){
        if (terminateReason.getText().toString().isEmpty()){
            terminateReason.setError(getString(R.string.terminate_error));
        }else{
            saveData();
            finish();
        }
    }

    private void saveData(){
        String courseKey = getIntent().getStringExtra("courseKey");
        String courseTime = getIntent().getStringExtra("courseTime");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user!=null && courseKey!=null){
            FirebaseDatabase.getInstance().getReference("Terminate").push().setValue(new Terminate(terminateReason.getText().toString(), user.getUid(), courseKey, dateTime, timestamp, courseTime, "processing"));
            FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("student").child("courses").child(courseKey).setValue("terminate");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.terminateRequest){
            checkEditText();
        }
    }

    private void getCurrentDateTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", Locale.ENGLISH);

        dateTime = simpleDateFormat.format(new java.util.Date());

        timestamp = System.currentTimeMillis()/1000;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}