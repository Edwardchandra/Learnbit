package com.example.learnbit.launch.student.home.category.subcategory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.student.home.category.adapter.AllCourseAdapter;
import com.example.learnbit.launch.student.home.search.StudentSearchActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubcategoryActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView subcategoryRecyclerView;
    private AllCourseAdapter allCourseAdapter;
    private ArrayList<Course> courseArrayList = new ArrayList<>();
    private ArrayList<String> keyArrayList = new ArrayList<>();
    private String subcategoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcategory);

        Button searchBarButton = findViewById(R.id.subcategorySearchBarButton);
        Button backButton = findViewById(R.id.backButton);
        subcategoryRecyclerView = findViewById(R.id.allSubcategoryCourseRecyclerView);

        searchBarButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        setStatusBarColor();
        retrieveIntentData();
        setupRecyclerView();
        retrieveData();
    }

    private void setupRecyclerView(){
        allCourseAdapter = new AllCourseAdapter(courseArrayList, keyArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        subcategoryRecyclerView.setLayoutManager(layoutManager);
        subcategoryRecyclerView.setAdapter(allCourseAdapter);
    }

    private void retrieveIntentData(){
        subcategoryName = getIntent().getStringExtra("subcategoryName");
    }

    private void retrieveData(){
        FirebaseDatabase.getInstance().getReference("Course").orderByChild("courseSubcategory").equalTo(subcategoryName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String key = ds.getKey();
                    Course course = ds.getValue(Course.class);
                    if (course!=null){
                        if (course.getCourseAcceptance().equalsIgnoreCase("accepted")){
                            courseArrayList.add(new Course(course.getCourseName(), course.getCoursePrice(), course.getCourseImageURL(), course.getCourseStudent(), course.getCourseRating(), course.getCourseCategory()));
                            keyArrayList.add(key);
                        }
                    }
                    allCourseAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });
    }

    //show toast
    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setStatusBarColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().setStatusBarColor(Color.WHITE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.subcategorySearchBarButton:
                Intent intent = new Intent(this, StudentSearchActivity.class);
                startActivity(intent);
                break;
            case R.id.backButton:
                finish();
                break;
        }
    }
}