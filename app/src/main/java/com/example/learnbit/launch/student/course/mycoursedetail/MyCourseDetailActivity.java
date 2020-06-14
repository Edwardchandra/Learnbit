package com.example.learnbit.launch.student.course.mycoursedetail;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.extension.BaseActivity;
import com.example.learnbit.launch.extension.SinchService;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.reusableactivity.CallScreenActivity;
import com.example.learnbit.launch.student.home.coursedetails.courseinfo.adapter.StudentSectionAdapter;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.adapter.SectionAdapter;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.model.Section;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sinch.android.rtc.calling.Call;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

public class MyCourseDetailActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView sectionRecyclerView;
    private TextView myCourseName, myCourseCategory, myCourseStartTime, teacherName, teacherRatings, teacherCourseCount;
    private ImageView teacherImageView, myCourseImageView;
    private Toolbar myCourseToolbar;
    private Button callButton;

    private String courseName, key;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser user;

    String[] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    private static final int PERMISSIONS_CODE = 1;

    private ArrayList<Section> sectionArrayList = new ArrayList<>();

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
        setupFirebase();
        retrieveData();

        checkServiceEnabled();
    }

    private void setupFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    private void setupToolbar(){
        setSupportActionBar(myCourseToolbar);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void retrieveIntentData(){
        courseName = getIntent().getStringExtra("courseName");
        key = getIntent().getStringExtra("key");
    }

    private void retrieveData(){
        DatabaseReference databaseReference = firebaseDatabase.getReference("Course");
        DatabaseReference databaseReference1 = firebaseDatabase.getReference("Users").child(user.getUid()).child("student").child("courses");
        DatabaseReference  databaseReference2 = firebaseDatabase.getReference("Users").child(key).child("name");
        DatabaseReference databaseReference3 = firebaseDatabase.getReference("Users").child(key).child("teacher").child("rating");

        Query query = databaseReference.child(key).orderByChild("courseName").startAt(courseName);

        query.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sectionArrayList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String key = ds.getKey();
                    Course course = ds.getValue(Course.class);

                    if (key!=null && course!=null){
                        Log.d("coursekey", key);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                        String today = simpleDateFormat.format(Calendar.getInstance().getTime());

                        for (HashMap.Entry<String, String> courseSchedule : course.getCourseSchedule().entrySet()){
                            String scheduleDay = courseSchedule.getValue();

                            if (scheduleDay.equals(today)){
                                databaseReference1.child(key).child("courseTime").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String courseTime = dataSnapshot.getValue(String.class);

                                        if (courseTime!=null){
                                            myCourseStartTime.setText("Your course will start at " + courseTime + " today");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(MyCourseDetailActivity.this, "failed to retrieve course time.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                myCourseStartTime.setText("You have no course for today");
                            }
                        }

                        for (HashMap.Entry<String, Section> entry : course.getCourseCurriculum().entrySet()) {
                            String curriculumKey = entry.getKey();
                            Section value = entry.getValue();

                            sectionArrayList.add(new Section(curriculumKey, value.getName(), value.getTopics()));
                            sectionArrayList.sort(Comparator.comparing(Section::getWeek));

                            setupRecyclerView();
                        }

                        myCourseName.setText(course.getCourseName());
                        myCourseCategory.setText(getString(R.string.divider, course.getCourseCategory(), course.getCourseSubcategory()));
                        Glide.with(getApplicationContext()).load(course.getCourseImageURL()).into(myCourseImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);

                if (name!=null) teacherName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MyCourseDetailActivity.this, "failed to retrieve teacher name", Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Float rating = dataSnapshot.getValue(float.class);

                if (rating!=null){
                    teacherRatings.setText(getString(R.string.teacher_rating, rating));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MyCourseDetailActivity.this, "failed to retrieve teacher rating", Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();

                teacherCourseCount.setText(getString(R.string.course_count, count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MyCourseDetailActivity.this, "failed to retrieve course count", Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference("Users").child(key).child("profileimage");

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(getApplicationContext()).load(uri).into(teacherImageView)).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "failed to load teacher image", Toast.LENGTH_SHORT).show());

    }

    private void setupRecyclerView(){
        SectionAdapter sectionAdapter = new SectionAdapter(sectionArrayList);
        RecyclerView.LayoutManager sectionLayoutManager = new LinearLayoutManager(getApplicationContext());
        sectionRecyclerView.setLayoutManager(sectionLayoutManager);
        sectionRecyclerView.setAdapter(sectionAdapter);
        sectionRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.callButton){
            if (!checkPermission(getApplicationContext(), permissions)){
                ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_CODE);
            }else{
                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
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
        if (key!=null){
            Log.d("callid", key);

            Call call = getSinchServiceInterface().callUserVideo(key);

            String callID = call.getCallId();

            Intent callScreen = new Intent(this, CallScreenActivity.class);
            callScreen.putExtra(SinchService.CALL_ID, callID);
            startActivity(callScreen);
        }
    }

    public static boolean checkPermission(Context context, String[] permissions){
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_CODE){
            if (grantResults.length > 0){
                callAction();
            }else{
                Toast.makeText(this, "please agree with the permissions", Toast.LENGTH_SHORT).show();
            }
        }
    }
}