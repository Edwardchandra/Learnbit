package com.example.learnbit.launch.teacher.home.coursedetail;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.extension.BaseActivity;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.teacher.TeacherMainActivity;
import com.example.learnbit.launch.teacher.home.coursedetail.adapter.CourseDetailViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CourseDetailActivity extends BaseActivity {

    //initiate elements variables
    private Toolbar courseDetailToolbar;
    private ViewPager courseDetailViewPager;
    private TabLayout courseDetailTabLayout;
    private ImageView courseDetailImageView;
    private TextView courseNameET, courseCategoryET, courseStatus;
    private LinearLayout linearLayout;

    //initiate firebase variables
    private FirebaseUser user;
    private Query query;

    //initiate class variable
    private Course course = new Course();

    //execute when activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        courseDetailToolbar = findViewById(R.id.teacherCourseDetail_Toolbar);
        courseDetailViewPager = findViewById(R.id.teacherCourseDetail_ViewPager);
        courseDetailTabLayout = findViewById(R.id.teacherCourseDetail_TabLayout);
        courseDetailImageView = findViewById(R.id.courseDetail_ImageView);
        courseNameET = findViewById(R.id.courseDetail_CourseTitle);
        courseCategoryET = findViewById(R.id.courseDetail_CourseCategory);
        linearLayout = findViewById(R.id.courseDetail_LinearLayout);
        courseStatus = findViewById(R.id.courseStatus);

        setupToolbar();
        setupViewPager();
        setupFirebase();
        retrieveData();
    }

    //setting up firebase instance
    private void setupFirebase(){

        //get course key from intent passing
        String courseKey = getIntent().getStringExtra("key");

        //initiate firebase instance
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        //get current user from firebase auth
        user = firebaseAuth.getCurrentUser();

        //get reference path to course data
        DatabaseReference databaseReference = firebaseDatabase.getReference("Course");
        if (courseKey!=null){
            query = databaseReference.child(courseKey);
        }
    }

    //retrieve course data from firebase database with given path
    private void retrieveData(){
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //get data as course object
                course = dataSnapshot.getValue(Course.class);

                //check if course is not null
                if (course != null){
                    //get current time
                    Date currentTime = Calendar.getInstance().getTime();

                    //initiate new date
                    Date startDate = new Date();
                    Date endDate = new Date();

                    //date formatter
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);

                    for (HashMap.Entry<String, String> entry : course.getCourseDate().entrySet()){
                        if (entry.getKey().equals("startDate")){
                            try {
                                startDate = simpleDateFormat1.parse(entry.getValue());
                            }catch (ParseException e){
                                e.printStackTrace();
                            }
                        }else if (entry.getKey().equals("endDate")){
                            try {
                                endDate = simpleDateFormat1.parse(entry.getValue());
                            }catch (ParseException e){
                                e.printStackTrace();
                            }
                        }
                    }

                    if (currentTime.after(endDate)){
                        linearLayout.setVisibility(View.VISIBLE);
                        courseStatus.setText(getString(R.string.course_end_period_status));
                    }

                    //set course image view
                    Glide.with(getApplicationContext()).load(course.getCourseImageURL()).into(courseDetailImageView);

                    //check if course acceptance is still pending/not
                    if (course.getCourseAcceptance().equals("pending")){
                        //if yes, set status visible
                        linearLayout.setVisibility(View.VISIBLE);
                    }else{
                        //set status invinsible
                        linearLayout.setVisibility(View.INVISIBLE);
                    }

                    //set course name and category - subcategory
                    courseNameET.setText(course.getCourseName());
                    courseCategoryET.setText(getString(R.string.divider, course.getCourseCategory(), course.getCourseSubcategory()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });
    }

    //delete course data
    private void deleteCourse(){
        //get passed intent data
        String courseName = getIntent().getStringExtra("courseName");

        //delete data from firebase database
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.delete_failed));
            }
        });

        //delete course image from firebase storage
        if (courseName!=null){
            FirebaseStorage.getInstance().getReference("Course").child(user.getUid()).child(courseName).child("courseImage").delete();
        }
    }

    //setting up custom toolbar
    private void setupToolbar(){
        setSupportActionBar(courseDetailToolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    //set up view pager to show different tab layout views
    private void setupViewPager(){
        CourseDetailViewPagerAdapter courseDetailViewPagerAdapter = new CourseDetailViewPagerAdapter(getSupportFragmentManager(), 0);
        courseDetailViewPager.setAdapter(courseDetailViewPagerAdapter);
        courseDetailTabLayout.setupWithViewPager(courseDetailViewPager);
    }

    //right dot menu action goes here
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.delete_menu:
                deleteCourse();
                Intent intent = new Intent(this, TeacherMainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //inflate right dot menu to toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.teacher_course_detail_menu, menu);
        return true;
    }

    //check if sinch service is connected
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    //to show toast
    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
