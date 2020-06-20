package com.example.learnbit.launch.student.course.mycoursedetail.rating;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.reviewtab.model.CourseReview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class RatingActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private RatingBar ratingBar;
    private TextView ratingStatus;
    private EditText review;
    private String courseKey, dateTime, teacherUid;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private long reviewCount;
    private float overallCourseRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        toolbar = findViewById(R.id.ratingToolbar);
        ratingBar = findViewById(R.id.ratingBar);
        ratingStatus = findViewById(R.id.ratingStatus);
        review = findViewById(R.id.review);
        Button submitButton = findViewById(R.id.submitReview);

        submitButton.setOnClickListener(this);

        getCurrentDateTime();
        setupToolbar();
        retrieveIntentData();
        setupFirebase();
        setupRatingBar();
        retrieveData();
    }

    private void setupRatingBar(){
        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (rating == 0){
                ratingStatus.setText(getString(R.string.rating_status_0));
            }else if (rating == 1){
                ratingStatus.setText(getString(R.string.rating_status_1));
            }else if (rating == 2){
                ratingStatus.setText(getString(R.string.rating_status_2));
            }else if (rating == 3){
                ratingStatus.setText(getString(R.string.rating_status_3));
            }else if (rating == 4){
                ratingStatus.setText(getString(R.string.rating_status_4));
            }else if (rating == 5){
                ratingStatus.setText(getString(R.string.rating_status_5));
            }
        });
    }

    private void setupToolbar(){
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Course Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void retrieveIntentData(){
        courseKey = getIntent().getStringExtra("courseKey");
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setupFirebase(){
        databaseReference = FirebaseDatabase.getInstance().getReference("Rating").child(courseKey);
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void retrieveData(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewCount = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });

        FirebaseDatabase.getInstance().getReference("Course").child(courseKey).child("courseRating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Float courseRating = dataSnapshot.getValue(Float.class);
                if (courseRating!=null){
                    overallCourseRating = courseRating;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });

        FirebaseDatabase.getInstance().getReference("Course").child(courseKey).child("teacherUid").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teacherUid = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submitReview){
            databaseReference.child("review " + (reviewCount + 1)).setValue(new CourseReview(review.getText().toString(), ratingBar.getRating(), dateTime, user.getUid()));
            FirebaseDatabase.getInstance().getReference("Course").child(courseKey).child("courseRating").setValue(((overallCourseRating + ratingBar.getRating())/2));
            FirebaseDatabase.getInstance().getReference("Users").child(teacherUid).child("teacher").child("rating").setValue(((overallCourseRating + ratingBar.getRating())/2));
            finish();
        }
    }

    private void getCurrentDateTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", Locale.ENGLISH);
        dateTime = simpleDateFormat.format(new java.util.Date());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}