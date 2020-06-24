package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.submitscore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.learnbit.R;
import com.google.firebase.database.FirebaseDatabase;

public class SubmitScoreActivity extends AppCompatActivity {

    private Toolbar scoreToolbar;
    private EditText performanceScore, attitudeScore;
    private String courseKey, studentUid, topic;

    //initiate preference key to retrieve shared preference data
    private static final String detailPreference = "DETAIL_PREFERENCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_score);

        scoreToolbar = findViewById(R.id.scoreToolbar);
        performanceScore = findViewById(R.id.performanceScore);
        attitudeScore = findViewById(R.id.attitudeScore);
        Button submitScore = findViewById(R.id.submitScore);

        setupToolbar();
        getPreferenceData();
        retrieveIntentData();

        submitScore.setOnClickListener(v -> {
            int performance = Integer.parseInt(performanceScore.getText().toString());
            int attitude = Integer.parseInt(attitudeScore.getText().toString());
            int totalScore = (performance + attitude)/2;
            FirebaseDatabase.getInstance().getReference("Users").child(studentUid).child("student").child("score").child(courseKey).child(topic).setValue(totalScore);
            Toast.makeText(this, "Score successfully submitted", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void setupToolbar(){
        setSupportActionBar(scoreToolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().setStatusBarColor(Color.WHITE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void retrieveIntentData(){
        studentUid = getIntent().getStringExtra("studentUid");
    }

    //retrieve stored data from shared preference
    private void getPreferenceData(){
        String preferenceKey = "courseKey";

        SharedPreferences preferences = getSharedPreferences(detailPreference, Context.MODE_PRIVATE);
        courseKey = preferences.getString(preferenceKey, "");

        SharedPreferences preferences1 = getSharedPreferences("SECTION_TOPIC_PREFERENCE", Context.MODE_PRIVATE);
        topic = preferences1.getString("topic", "");
    }
}