package com.example.learnbit.launch.student.home.coursedetails;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.student.home.coursedetails.adapter.StudentCourseDetailViewPagerAdapter;
import com.example.learnbit.launch.student.home.coursedetails.coursecheckout.StudentCheckoutActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class StudentCourseDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar courseDetailToolbar;
    private ImageView courseDetailImageView;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView courseNameET, courseCategoryET, coursePriceET, courseQuotaET;

    private FirebaseDatabase firebaseDatabase;

    private Course course = new Course();

    private String key;

    private static final String detailPreference = "STUDENT_DETAIL_PREFERENCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_course_detail);

        viewPager = findViewById(R.id.studentCourseDetail_ViewPager);
        tabLayout = findViewById(R.id.studentCourseDetail_TabLayout);

        courseDetailToolbar = findViewById(R.id.teacherCourseDetail_Toolbar);
        courseDetailImageView = findViewById(R.id.courseDetail_ImageView);
        courseNameET = findViewById(R.id.courseDetail_CourseTitle);
        courseCategoryET = findViewById(R.id.courseDetail_CourseCategory);

        coursePriceET = findViewById(R.id.courseDetailPrice);
        courseQuotaET = findViewById(R.id.courseDetailAvailableSchedule);
        Button enrollButton = findViewById(R.id.enrollButton);

        enrollButton.setOnClickListener(this);

        setupToolbar();
        setupViewPager();
        setupFirebase();
        retrieveData();
    }

    private void setupFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void retrieveData(){
        key = getIntent().getStringExtra("key");

        if (key!=null){
            DatabaseReference databaseReference = firebaseDatabase.getReference("Course").child(key);

            ValueEventListener retrieveEventListener = new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    course = dataSnapshot.getValue(Course.class);

                    if (course != null) {
                        Glide.with(getApplicationContext()).load(course.getCourseImageURL()).into(courseDetailImageView);

                        courseNameET.setText(course.getCourseName());
                        courseCategoryET.setText(getString(R.string.divider, course.getCourseCategory(), course.getCourseSubcategory()));

                        coursePriceET.setText(getString(R.string.price, course.getCoursePrice()));

                        int counter = 0;

                        for (HashMap.Entry<String, Boolean> entry : course.getCourseTime().entrySet()) {
                            Boolean value = entry.getValue();

                            if (!value) {
                                counter = counter + 1;
                            }
                        }

                        courseQuotaET.setText(getString(R.string.schedule_availability, counter));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(StudentCourseDetailActivity.this, getString(R.string.retrieve_failed), Toast.LENGTH_SHORT).show();
                }
            };

            databaseReference.addValueEventListener(retrieveEventListener);
        }

        savePreferenceData();
    }

    private void savePreferenceData(){
        if (getIntent()!=null){
            SharedPreferences preferences = getSharedPreferences(detailPreference, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("courseKey", key);
            editor.apply();
        }
    }

    private void setupToolbar(){
        setSupportActionBar(courseDetailToolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupViewPager(){
        StudentCourseDetailViewPagerAdapter courseDetailViewPagerAdapter = new StudentCourseDetailViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPager.setAdapter(courseDetailViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.enrollButton) {
            Intent intent = new Intent(getApplicationContext(), StudentCheckoutActivity.class);
            intent.putExtra("courseKey", key);
            intent.putExtra("price", coursePriceET.getText().toString());
            startActivity(intent);
        }
    }
}
