package com.example.learnbit.launch.student.course.mycoursedetail;

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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.extension.BaseActivity;
import com.example.learnbit.launch.extension.SinchService;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.reusableactivity.CallScreenActivity;
import com.example.learnbit.launch.student.course.adapter.MyCourseSectionAdapter;
import com.example.learnbit.launch.student.course.mycoursedetail.rating.RatingActivity;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.adapter.SectionAdapter;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.model.Section;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.sinch.android.rtc.calling.Call;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MyCourseDetailActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView sectionRecyclerView;
    private TextView myCourseName, myCourseCategory, myCourseStartTime, teacherName, teacherRatings, teacherCourseCount;
    private ImageView teacherImageView, myCourseImageView;
    private Toolbar myCourseToolbar;
    private Button callButton;

    private String key;
    private String teacherUid;

    private FirebaseDatabase firebaseDatabase;

    private MyCourseSectionAdapter sectionAdapter;
    private ArrayList<Section> sectionArrayList = new ArrayList<>();

    private static final String detailPreference = "DETAIL_PREFERENCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_course_detail);

        myCourseName = findViewById(R.id.myCourseDetail_CourseTitle);
        myCourseCategory = findViewById(R.id.myCourseDetail_CourseCategory);
        myCourseStartTime = findViewById(R.id.myCourseStartTime);
        teacherName = findViewById(R.id.teacherName);
        teacherRatings = findViewById(R.id.teacherRatings);
        teacherImageView = findViewById(R.id.teacherImageView);
        myCourseImageView = findViewById(R.id.myCourseDetail_ImageView);
        teacherCourseCount = findViewById(R.id.teacherCourseCount);
        callButton = findViewById(R.id.callButton);
        sectionRecyclerView = findViewById(R.id.curriculumRecyclerView);
        myCourseToolbar = findViewById(R.id.myCourseDetail_Toolbar);

        callButton.setEnabled(false);
        callButton.setOnClickListener(this);

        teacherImageView.setClipToOutline(true);

        setupToolbar();
        retrieveIntentData();
        savePreferenceData();
        setupFirebase();
        setupRecyclerView();
        retrieveData();
        checkServiceEnabled();
    }

    private void setupFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void setupToolbar(){
        setSupportActionBar(myCourseToolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Course Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void retrieveIntentData(){
        key = getIntent().getStringExtra("key");
    }

    private void retrieveData() {
        sectionArrayList.clear();

        DatabaseReference databaseReference = firebaseDatabase.getReference("Course").child(key);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Course course = dataSnapshot.getValue(Course.class);

                if (course != null) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                    String today = simpleDateFormat.format(Calendar.getInstance().getTime());

                    String startDateString = "", endDateString = "";
                    Date startDate = new Date(), endDate = new Date();
                    Date date = Calendar.getInstance().getTime();

                    for (HashMap.Entry<String, String> courseDate : course.getCourseDate().entrySet()){
                        if (courseDate.getKey().equals("startDate")){
                            startDateString = courseDate.getValue();
                        }else if (courseDate.getKey().equals("endDate")){
                            endDateString = courseDate.getValue();
                        }
                    }

                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);

                    try {
                        startDate = simpleDateFormat1.parse(startDateString);
                        endDate = simpleDateFormat1.parse(endDateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (date.after(startDate) && date.before(endDate)) {
                        for (HashMap.Entry<String, String> entry : course.getCourseSchedule().entrySet()){
                            String schedule = entry.getValue();

                            if (schedule.equals(today)){

                                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);

                                FirebaseDatabase.getInstance().getReference("Users").child(course.getTeacherUid()).child("student").child("courses").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String time = dataSnapshot.getValue(String.class);
                                        if (time!=null){
                                            Date courseTime = new Date();
                                            try {
                                                courseTime = simpleDateFormat2.parse(time);
                                            }catch (ParseException e){
                                                e.printStackTrace();
                                            }

                                            if (!date.after(courseTime)){
                                                myCourseStartTime.setText(getString(R.string.course_start_time, time));
                                            }else{
                                                myCourseStartTime.setText(getString(R.string.no_course));
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        toast(getString(R.string.retrieve_failed));
                                    }
                                });
                            }else{
                                myCourseStartTime.setText(getString(R.string.no_course));
                            }
                        }
                    }else if (date.before(startDate)){
                        myCourseStartTime.setText(getString(R.string.course_start_period));
                        callButton.setVisibility(View.GONE);
                    }else if (date.after(endDate)){
                        myCourseStartTime.setText(getString(R.string.course_end_period));
                        callButton.setText(getString(R.string.call_button_rating));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            callButton.setBackgroundColor(getColor(R.color.orangeColor));
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        for (HashMap.Entry<String, Section> entry : course.getCourseCurriculum().entrySet()) {
                            sectionArrayList.add(new Section(entry.getKey(), entry.getValue().getName(), entry.getValue().getTopics()));
                            sectionArrayList.sort(Comparator.comparing(Section::getWeek));
                        }
                    }

                    teacherUid = course.getTeacherUid();
                    myCourseName.setText(course.getCourseName());
                    myCourseCategory.setText(getString(R.string.divider, course.getCourseCategory(), course.getCourseSubcategory()));
                    Glide.with(getApplicationContext()).load(course.getCourseImageURL()).into(myCourseImageView);

                    firebaseDatabase.getReference("Users").child(course.getTeacherUid()).child("name").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.getValue(String.class);
                            if (name!=null){
                                teacherName.setText(name);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            toast(getString(R.string.retrieve_failed));
                        }
                    });

                    firebaseDatabase.getReference("Users").child(course.getTeacherUid()).child("teacher").child("rating").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Float rating = dataSnapshot.getValue(Float.class);
                            if (rating!=null){
                                teacherRatings.setText(getString(R.string.teacher_rating, rating));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            toast(getString(R.string.retrieve_failed));
                        }
                    });

                    firebaseDatabase.getReference("Course").orderByChild("teacherUid").equalTo(course.getTeacherUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            long courseCount = dataSnapshot.getChildrenCount();
                            teacherCourseCount.setText(getString(R.string.course_count, courseCount));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            toast(getString(R.string.retrieve_failed));
                        }
                    });

                    FirebaseStorage.getInstance().getReference("Users").child(course.getTeacherUid()).child("profileimage").getDownloadUrl()
                            .addOnSuccessListener(uri -> Glide.with(getApplicationContext()).load(uri).into(teacherImageView))
                            .addOnFailureListener(e -> toast(getString(R.string.retrieve_failed)));

                    sectionAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });
    }

    private void setupRecyclerView(){
        sectionAdapter = new MyCourseSectionAdapter(sectionArrayList);
        RecyclerView.LayoutManager sectionLayoutManager = new LinearLayoutManager(this);
        sectionRecyclerView.setLayoutManager(sectionLayoutManager);
        sectionRecyclerView.setAdapter(sectionAdapter);
        sectionRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.callButton){
            if (!callButton.getText().toString().equals(getString(R.string.call_button_rating))){
                Intent intent = new Intent(this, RatingActivity.class);
                intent.putExtra("courseKey", key);
                startActivity(intent);
            }else{
                callAction();
            }
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        callButton.setEnabled(true);
        checkServiceEnabled();
    }

    private void checkServiceEnabled(){
        if (!callButton.isEnabled()){
            callButton.setClickable(false);
            callButton.setBackground(getResources().getDrawable(R.drawable.call_button_disabled));
        }else{
            callButton.setClickable(true);
            callButton.setBackground(getResources().getDrawable(R.drawable.call_button));
        }
    }

    private void callAction(){
        if (teacherUid!=null){
            Call call = getSinchServiceInterface().callUserVideo(teacherUid);
            String callID = call.getCallId();
            Intent callScreen = new Intent(this, CallScreenActivity.class);
            callScreen.putExtra(SinchService.CALL_ID, callID);
            startActivity(callScreen);
        }
    }

    //show toast
    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void savePreferenceData(){
        SharedPreferences preferences = getSharedPreferences(detailPreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("courseKey", key);
        editor.apply();
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