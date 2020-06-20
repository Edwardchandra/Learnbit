package com.example.learnbit.launch.student.home.category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.student.home.adapter.CourseCardAdapter;
import com.example.learnbit.launch.student.home.category.adapter.AllCourseAdapter;
import com.example.learnbit.launch.student.home.category.adapter.SubcategoryAdapter;
import com.example.learnbit.launch.student.home.model.Category;
import com.example.learnbit.launch.student.home.search.StudentSearchActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView subcategoryRecyclerView, featuredRecyclerView, allCourseRecyclerView;
    private CourseCardAdapter courseCardAdapter;
    private AllCourseAdapter allCourseAdapter;
    private ArrayList<Course> allCourseArrayList = new ArrayList<>();
    private ArrayList<Category> subcategoryArrayList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private ArrayList<Course> courseArrayList = new ArrayList<>();
    private ArrayList<String> keyArrayList = new ArrayList<>();
    private ArrayList<String> allKeyArrayList = new ArrayList<>();
    private String categoryName;
    private TextView featureCourse, allCourse;
    private TextView noCategoryCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        featureCourse = findViewById(R.id.featureCourseName);
        noCategoryCourse = findViewById(R.id.noCategoryCourse);
        allCourse = findViewById(R.id.allCourseName);
        subcategoryRecyclerView = findViewById(R.id.subcategoryRecyclerView);
        featuredRecyclerView = findViewById(R.id.featuredRecyclerView);
        allCourseRecyclerView = findViewById(R.id.allCourseRecyclerView);
        Button searchButton = findViewById(R.id.categorySearchBarButton);
        Button backButton = findViewById(R.id.backButton);

        searchButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        setStatusBarColor();
        retrieveIntentData();
        setupFirebase();
        setupRecyclerView();
        addData();
        retrieveData();
    }

    private void retrieveIntentData(){
        categoryName = getIntent().getStringExtra("categoryName");
    }

    private void addData(){
        if (categoryName!=null){
            String filterCategoryName = categoryName.replace("\n", "");

            switch (filterCategoryName){
                case "Language and Literature":
                    subcategoryArrayList.add(new Category(R.drawable.indonesia, "Indonesia"));
                    subcategoryArrayList.add(new Category(R.drawable.english, "English"));
                    subcategoryArrayList.add(new Category(R.drawable.mandarin, "Mandarin"));
                    break;
                case "Personal Development":
                    subcategoryArrayList.add(new Category(R.drawable.self_esteem, "Self Esteem"));
                    subcategoryArrayList.add(new Category(R.drawable.physical_edu, "Physical Education"));
                    break;
                case "Computer Technology":
                    subcategoryArrayList.add(new Category(R.drawable.it_software, "IT and Software"));
                    subcategoryArrayList.add(new Category(R.drawable.graphic_design, "Graphic Design"));
                    subcategoryArrayList.add(new Category(R.drawable.office, "Office"));
                    subcategoryArrayList.add(new Category(R.drawable.photo_editing, "Photo and Video Editing"));
                    break;
                case "Mathematics and Logic":
                    subcategoryArrayList.add(new Category(R.drawable.general_math, "General Mathematics"));
                    subcategoryArrayList.add(new Category(R.drawable.discrete_math, "Discrete Mathematics"));
                    subcategoryArrayList.add(new Category(R.drawable.statistic, "Statistic"));
                    break;
                case "Natural Science":
                    subcategoryArrayList.add(new Category(R.drawable.math_science, "Mathematical Sciences"));
                    subcategoryArrayList.add(new Category(R.drawable.biology, "Biology"));
                    subcategoryArrayList.add(new Category(R.drawable.chemistry, "Chemistry"));
                    subcategoryArrayList.add(new Category(R.drawable.physics, "Physics"));
                    break;
                case "Social Science":
                    subcategoryArrayList.add(new Category(R.drawable.history, "History"));
                    subcategoryArrayList.add(new Category(R.drawable.geography, "Geography"));
                    subcategoryArrayList.add(new Category(R.drawable.sociology, "Sociology"));
                    subcategoryArrayList.add(new Category(R.drawable.economics, "Economics"));
                    break;
                case "Art and Culture":
                    subcategoryArrayList.add(new Category(R.drawable.music, "Music"));
                    subcategoryArrayList.add(new Category(R.drawable.painting, "Painting and Sculpture"));
                    subcategoryArrayList.add(new Category(R.drawable.craft, "Craftsmanship"));
                    break;
                case "Civic Education":
                    subcategoryRecyclerView.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void setupRecyclerView(){
        SubcategoryAdapter subcategoryAdapter = new SubcategoryAdapter(subcategoryArrayList);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 4);
        subcategoryRecyclerView.setLayoutManager(layoutManager);
        subcategoryRecyclerView.setAdapter(subcategoryAdapter);

        courseCardAdapter = new CourseCardAdapter(courseArrayList, keyArrayList);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        featuredRecyclerView.setLayoutManager(layoutManager1);
        featuredRecyclerView.setAdapter(courseCardAdapter);

        allCourseAdapter = new AllCourseAdapter(allCourseArrayList, allKeyArrayList);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this);
        allCourseRecyclerView.setLayoutManager(layoutManager2);
        allCourseRecyclerView.setAdapter(allCourseAdapter);
    }

    private void setupFirebase(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Course");
    }

    private void retrieveData(){
        Query query = databaseReference.orderByChild("courseCategory").equalTo(categoryName.replace("\n", "")).limitToLast(5);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String key = ds.getKey();
                    Course course = ds.getValue(Course.class);
                    if (course!=null){
                        if (course.getCourseAcceptance().equalsIgnoreCase("accepted")){
                            allCourseArrayList.add(new Course(course.getCourseName(), course.getCoursePrice(), course.getCourseImageURL(), course.getCourseStudent(), course.getCourseRating(), course.getCourseCategory()));
                            allKeyArrayList.add(key);
                            if (course.getCourseRating() >= 4){
                                courseArrayList.add(new Course(course.getCourseName(), course.getCoursePrice(), course.getCourseImageURL(), course.getCourseStudent(), course.getCourseRating(), course.getCourseCategory()));
                                keyArrayList.add(key);
                            }
                        }
                    }
                    allCourseAdapter.notifyDataSetChanged();
                    courseCardAdapter.notifyDataSetChanged();
                }

                if (courseArrayList.size()==0){
                    featuredRecyclerView.setVisibility(View.GONE);
                    featureCourse.setVisibility(View.GONE);
                }

                if (allCourseArrayList.size()==0){
                    allCourseRecyclerView.setVisibility(View.GONE);
                    allCourse.setVisibility(View.GONE);
                }

                if (courseArrayList.size()==0 && allCourseArrayList.size()==0){
                    noCategoryCourse.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toast(getString(R.string.retrieve_failed));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.categorySearchBarButton:
                Intent intent = new Intent(this, StudentSearchActivity.class);
                startActivity(intent);
                break;
            case R.id.backButton:
                finish();
                break;
        }
    }

    private void setStatusBarColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().setStatusBarColor(Color.WHITE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    //show toast
    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}