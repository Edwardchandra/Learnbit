package com.example.learnbit.launch.student.teacherprofile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.model.userdata.User;
import com.example.learnbit.launch.model.userdata.teacher.Teacher;
import com.example.learnbit.launch.student.course.adapter.MyCourseAdapter;
import com.example.learnbit.launch.student.course.mycoursedetail.terminate.TerminateActivity;
import com.example.learnbit.launch.student.home.category.adapter.AllCourseAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class TeacherProfileActivity extends AppCompatActivity {

    private TextView teacherName, teacherEmail, teacherRatings, teacherDescription, allCourseName;
    private RecyclerView allCourseRecyclerView;
    private ImageView teacherImageView, backgroundImage;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private String teacherUid;
    private Toolbar profileToolbar;
    private AllCourseAdapter courseAdapter;
    private ArrayList<Course> courseArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        allCourseName = findViewById(R.id.allCourseName);
        allCourseRecyclerView = findViewById(R.id.allCourseRecyclerView);
        profileToolbar = findViewById(R.id.profileToolbar);
        backgroundImage = findViewById(R.id.backgroundImage);
        teacherName = findViewById(R.id.teacherName);
        teacherEmail = findViewById(R.id.teacherEmail);
        teacherRatings = findViewById(R.id.teacherRatings);
        teacherDescription = findViewById(R.id.teacherDescription);
        teacherImageView = findViewById(R.id.teacherImageView);

        teacherImageView.setClipToOutline(true);

        setupToolbar();
        setupFirebase();
        setupRecyclerView();
        retrieveIntentData();
        retrieveData();
        retrieveCourseData();
    }

    private void setupFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
    }

    private void retrieveIntentData(){
        teacherUid = getIntent().getStringExtra("teacherUid");
    }

    private void retrieveData(){
        firebaseStorage.getReference().child("Users").child(teacherUid).child("profileimage").getDownloadUrl()
                .addOnFailureListener(e -> toast(getString(R.string.retrieve_failed)))
                .addOnSuccessListener(uri -> {
                    Glide.with(getApplicationContext()).load(uri).into(teacherImageView);
                    Glide.with(getApplicationContext()).load(uri).apply(RequestOptions.bitmapTransform(new BlurTransformation(25,3))).into(backgroundImage);
                });

        firebaseDatabase.getReference("Users").child(teacherUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user!=null){
                    teacherName.setText(user.getName());
                    teacherEmail.setText(user.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });

        firebaseDatabase.getReference("Users").child(teacherUid).child("teacher").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Teacher teacher = dataSnapshot.getValue(Teacher.class);
                if (teacher!=null){
                    teacherRatings.setText(getString(R.string.teacher_rating, teacher.getRating()));
                    teacherDescription.setText(teacher.getDescription());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });
    }

    private void setupRecyclerView(){
        courseAdapter = new AllCourseAdapter(courseArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        allCourseRecyclerView.setLayoutManager(layoutManager);
        allCourseRecyclerView.setAdapter(courseAdapter);
    }

    private void retrieveCourseData(){
        Query query = FirebaseDatabase.getInstance().getReference("Course").orderByChild("teacherUid").startAt(teacherUid);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String key = ds.getKey();
                    Course course = ds.getValue(Course.class);
                    if (course!=null){
                        if (course.getCourseAcceptance().equalsIgnoreCase("accepted")){
                            if (course.getCourseTime().containsValue(false)){
                                courseArrayList.add(new Course(key, course.getCourseName(), course.getCoursePrice(), course.getCourseImageURL(), course.getCourseStudent(), course.getCourseRating(), course.getCourseCategory()));
                            }
                        }
                    }
                    courseAdapter.notifyDataSetChanged();
                }

                if (courseArrayList.size()==0){
                    allCourseRecyclerView.setVisibility(View.GONE);
                    allCourseName.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });
    }

    //setting up custom toolbar for activity
    private void setupToolbar(){
        setSupportActionBar(profileToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}