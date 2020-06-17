package com.example.learnbit.launch.teacher.home.addcourse.thirdsection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.addcourse.fourthsection.AddFourthSectionActivity;
import com.example.learnbit.launch.teacher.home.addcourse.secondsection.model.Time;
import com.example.learnbit.launch.teacher.home.addcourse.thirdsection.adapter.CourseBenefitAdapter;
import com.example.learnbit.launch.teacher.home.addcourse.thirdsection.adapter.CourseCurriculumAdapter;
import com.example.learnbit.launch.teacher.home.addcourse.thirdsection.adapter.CourseRequirementAdapter;
import com.example.learnbit.launch.teacher.home.addcourse.thirdsection.model.Benefit;
import com.example.learnbit.launch.teacher.home.addcourse.thirdsection.model.Curriculum;
import com.example.learnbit.launch.teacher.home.addcourse.thirdsection.model.Requirement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class AddThirdSectionActivity extends AppCompatActivity implements View.OnClickListener {

    //elements variable
    private RecyclerView courseBenefitRecyclerView, courseRequirementRecyclerView, courseCurriculumRecyclerView;
    private Toolbar thirdSectionToolbar;

    //recyclerview adapter variable
    private CourseBenefitAdapter courseBenefitAdapter;
    private CourseRequirementAdapter courseRequirementAdapter;
    private CourseCurriculumAdapter courseCurriculumAdapter;

    //variables
    private ArrayList<Benefit> courseBenefitArrayList;
    private ArrayList<Requirement> courseRequirementArrayList;
    private ArrayList<Curriculum> courseCurriculumArrayList;
    private ArrayList<Time> courseTimeArrayList;
    private int weeks = 0;
    private String courseName;
    private String courseCategory;
    private String courseSubcategory;
    private String courseSummary;
    private String courseStartDate;
    private String courseEndDate;
    private String[] courseScheduleArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_third_section);

        courseBenefitRecyclerView = findViewById(R.id.courseBenefitRecyclerView);
        courseRequirementRecyclerView = findViewById(R.id.courseRequirementRecyclerView);
        courseCurriculumRecyclerView = findViewById(R.id.courseCurriculumRecyclerView);
        Button nextButton = findViewById(R.id.addCourse_NextButton);
        Button addCourseBenefitButton = findViewById(R.id.addCourse_AddCourseBenefitsButton);
        Button addCourseRequirementButton = findViewById(R.id.addCourse_AddCourseRequirementButton);
        thirdSectionToolbar = findViewById(R.id.thirdSectionToolbar);

        addCourseBenefitButton.setOnClickListener(this);
        addCourseRequirementButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        setupRecyclerViews();
        setupToolbar();
        retrieveIntentData();
        addEditText();
    }

    //setup custom toolbar
    private void setupToolbar(){
        setSupportActionBar(thirdSectionToolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().setStatusBarColor(Color.WHITE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    //retrieve passed intent data
    private void retrieveIntentData(){
        weeks = getIntent().getIntExtra("weekcount", 0);
        courseName = getIntent().getStringExtra("courseName");
        courseCategory = getIntent().getStringExtra("courseCategory");
        courseSubcategory = getIntent().getStringExtra("courseSubcategory");
        courseSummary = getIntent().getStringExtra("courseSummary");
        courseStartDate = getIntent().getStringExtra("courseStartDate");
        courseEndDate = getIntent().getStringExtra("courseEndDate");
        courseScheduleArray = getIntent().getStringArrayExtra("courseScheduleArray");
        courseTimeArrayList = getIntent().getParcelableArrayListExtra("courseTimeArrayList");
    }

    //toolbar button selected action defined here
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //setup recycler view layout type and adapter
    private void setupRecyclerViews(){
        courseBenefitAdapter = new CourseBenefitAdapter(this);
        RecyclerView.LayoutManager benefitLayoutManager = new LinearLayoutManager(this);
        courseBenefitRecyclerView.setLayoutManager(benefitLayoutManager);
        courseBenefitRecyclerView.setAdapter(courseBenefitAdapter);

        courseRequirementAdapter = new CourseRequirementAdapter(this);
        RecyclerView.LayoutManager requirementLayoutManager = new LinearLayoutManager(this);
        courseRequirementRecyclerView.setLayoutManager(requirementLayoutManager);
        courseRequirementRecyclerView.setAdapter(courseRequirementAdapter);

        courseCurriculumAdapter = new CourseCurriculumAdapter(this);
        RecyclerView.LayoutManager curriculumLayoutManager = new LinearLayoutManager(this);
        courseCurriculumRecyclerView.setLayoutManager(curriculumLayoutManager);
        courseCurriculumRecyclerView.setAdapter(courseCurriculumAdapter);
    }

    //retrieve curriculum edittext array list value
    private void getCurriculumArrayList(){
        courseCurriculumArrayList = courseCurriculumAdapter.getArrayList();

        for (int i=0;i<courseCurriculumArrayList.size();i++){

            String[] topics = new String[]{courseCurriculumArrayList.get(i).getTopicA(), courseCurriculumArrayList.get(i).getTopicB(), courseCurriculumArrayList.get(i).getTopicC()};
            String[] spinners = new String[]{courseCurriculumArrayList.get(i).getSpinnerA(), courseCurriculumArrayList.get(i).getSpinnerB(), courseCurriculumArrayList.get(i).getSpinnerC()};

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                topics = Arrays.stream(topics).filter(s -> (s != null && s.length() > 0)).toArray(String[]::new);
                spinners = Arrays.stream(spinners).filter(s -> (s != null && s.length() > 0)).toArray(String[]::new);
            }

            courseCurriculumArrayList.get(i).setTopics(topics);
            courseCurriculumArrayList.get(i).setSpinners(spinners);
        }

        courseCurriculumArrayList.removeAll(Collections.singleton(null));
    }

    //retrieve requirement edittext array list value
    private void getRequirementArrayList(){
        courseRequirementArrayList = courseRequirementAdapter.getArrayList();
    }

    //retrieve benefit edittext array list value
    private void getBenefitArrayList(){
        courseBenefitArrayList = courseBenefitAdapter.getArrayList();
    }

    //launch intent and send data through intent
    private void sendData(){
        Intent intent = new Intent(this, AddFourthSectionActivity.class);

        intent.putExtra("weekcount", weeks);
        intent.putExtra("courseName", courseName);
        intent.putExtra("courseCategory", courseCategory);
        intent.putExtra("courseSubcategory", courseSubcategory);
        intent.putExtra("courseSummary", courseSummary);
        intent.putExtra("courseStartDate", courseStartDate);
        intent.putExtra("courseEndDate", courseEndDate);
        intent.putExtra("courseScheduleArray", courseScheduleArray);
        intent.putParcelableArrayListExtra("courseTimeArrayList", courseTimeArrayList);
        intent.putParcelableArrayListExtra("courseBenefitArrayList", courseBenefitArrayList);
        intent.putParcelableArrayListExtra("courseRequirementArrayList", courseRequirementArrayList);
        intent.putParcelableArrayListExtra("courseCurriculumArrayList", courseCurriculumArrayList);

        startActivity(intent);
    }

    //element onclick action defined here
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addCourse_NextButton:
                validate();
                break;
            case R.id.addCourse_AddCourseBenefitsButton:
                Benefit benefit = new Benefit();
                courseBenefitAdapter.addEditText(benefit);
                break;
            case R.id.addCourse_AddCourseRequirementButton:
                Requirement requirement = new Requirement();
                courseRequirementAdapter.addEditText(requirement);
                break;
        }
    }

    //check if requirements, benefits, or curriculums is not specified
    private void validate(){
        getBenefitArrayList();
        getRequirementArrayList();
        getCurriculumArrayList();

        if (courseBenefitArrayList.size()==0){
            toast(getString(R.string.benefit_error));
        }else if (courseRequirementArrayList.size()==0){
            toast(getString(R.string.requirement_error));
        }else if (courseCurriculumArrayList.size()==0){
            toast(getString(R.string.curriculum_error));
        }else{
            sendData();
        }
    }

    //add new edittext to recyclerview on add button click
    private void addEditText(){
        for (int i=0;i<weeks;i++){
            Curriculum curriculum = new Curriculum();
            courseCurriculumAdapter.addEditText(curriculum);
        }
    }

    //show toast
    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
