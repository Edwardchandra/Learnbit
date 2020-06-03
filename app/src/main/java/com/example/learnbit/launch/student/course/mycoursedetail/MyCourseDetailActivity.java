package com.example.learnbit.launch.student.course.mycoursedetail;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.student.home.coursedetails.courseinfo.adapter.BenefitAdapter;
import com.example.learnbit.launch.student.home.coursedetails.courseinfo.adapter.RequirementAdapter;
import com.example.learnbit.launch.student.home.coursedetails.courseinfo.adapter.SectionAdapter;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

public class MyCourseDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView sectionRecyclerView;
    private TextView myCourseName, myCourseCategory, myCourseStartTime, teacherName, teacherRatings, teacherCourseCount;
    private ImageView teacherImageView, myCourseImageView;
    private Toolbar myCourseToolbar;

    private String courseName, key;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser user;

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
        Button callButton = findViewById(R.id.callButton);
        sectionRecyclerView = findViewById(R.id.curriculumRecyclerView);
        myCourseToolbar = findViewById(R.id.myCourseDetail_Toolbar);

        callButton.setOnClickListener(this);

        teacherImageView.setClipToOutline(true);

        setupToolbar();
        retrieveIntentData();
        setupFirebase();
        retrieveData();
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
            //do something here
            Log.d("call Button", "call action");
        }
    }
}