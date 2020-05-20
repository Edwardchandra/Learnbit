package com.example.learnbit.launch.teacher.home.coursedetail;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
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

public class CourseDetailActivity extends AppCompatActivity {

    private Toolbar courseDetailToolbar;
    private ViewPager courseDetailViewPager;
    private TabLayout courseDetailTabLayout;
    private ImageView courseDetailImageView;
    private TextView courseNameET, courseCategoryET;
    private LinearLayout linearLayout;

    private static final String detailPreference = "DETAIL_PREFERENCE";
    private String courseName;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Query query;

    private Course course = new Course();

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

        setupToolbar();
        setupViewPager();
        savePreferenceData();
        setupFirebase();
        retrieveData();
    }

    private void setupFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        user = firebaseAuth.getCurrentUser();
    }

    private void retrieveData(){
        databaseReference = firebaseDatabase.getReference("Course");
        query = databaseReference.child(user.getUid()).orderByChild("courseName").startAt(courseName);

        Log.d("courseName", courseName);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    course = ds.getValue(Course.class);

                    if (course != null){
                        String courseImageURL = course.getCourseImageURL();
                        Log.d("courseImageURL", courseImageURL + " ");
                        Glide.with(getApplicationContext()).load(courseImageURL).into(courseDetailImageView);

                        if (course.getCourseAcceptance().equals("pending")){
                            linearLayout.setVisibility(View.VISIBLE);
                        }else{
                            linearLayout.setVisibility(View.INVISIBLE);
                        }
                        courseNameET.setText(course.getCourseName());
                        courseCategoryET.setText(course.getCourseCategory() + " - " + course.getCourseSubcategory());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void deleteCourse(){
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    ds.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void savePreferenceData(){
        if (getIntent()!=null){
            SharedPreferences preferences = getSharedPreferences(detailPreference, MODE_PRIVATE);
            courseName = getIntent().getStringExtra("courseName");

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("courseName", courseName);
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
        CourseDetailViewPagerAdapter courseDetailViewPagerAdapter = new CourseDetailViewPagerAdapter(getSupportFragmentManager(), 0);
        courseDetailViewPager.setAdapter(courseDetailViewPagerAdapter);
        courseDetailTabLayout.setupWithViewPager(courseDetailViewPager);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.delete_menu:
                deleteCourse();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.teacher_course_detail_menu, menu);
        return true;
    }

}
