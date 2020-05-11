package com.example.learnbit.launch.teacher.home.addcourse.fourthsection;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.TeacherMainActivity;
import com.example.learnbit.launch.teacher.home.addcourse.fifthsection.AddFifthSectionActivity;
import com.example.learnbit.launch.teacher.home.addcourse.fourthsection.adapter.CourseTermsAdapter;
import com.example.learnbit.launch.teacher.home.addcourse.fourthsection.model.Terms;
import com.example.learnbit.launch.teacher.home.addcourse.secondsection.model.Time;
import com.example.learnbit.launch.teacher.home.addcourse.thirdsection.model.Benefit;
import com.example.learnbit.launch.teacher.home.addcourse.thirdsection.model.Curriculum;
import com.example.learnbit.launch.teacher.home.addcourse.thirdsection.model.Requirement;

import java.util.ArrayList;

public class AddFourthSectionActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView termsRecyclerView;
    private CheckBox termsCheckbox;
    private EditText priceET;
    private Toolbar fourthSectionToolbar;

    ArrayList<Terms> termsArrayList = new ArrayList<>();
    CourseTermsAdapter courseTermsAdapter;

    private String courseName;
    private String courseCategory;
    private String courseSubcategory;
    private String courseSummary;
    private String courseStartDate;
    private String courseEndDate;
    private String[] courseScheduleArray;
    private ArrayList<Time> courseTimeArrayList;
    private ArrayList<Benefit> courseBenefitArrayList;
    private ArrayList<Requirement> courseRequirementArrayList;
    private ArrayList<Curriculum> courseCurriculumArrayList;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fourth_section);

        termsRecyclerView = (RecyclerView) findViewById(R.id.addCourse_TermsConditionsRecyclerView);
        Button nextButton = (Button) findViewById(R.id.addCourse_NextButton);
        termsCheckbox = (CheckBox) findViewById(R.id.addCourse_TermsConditionsCheckbox);
        priceET = (EditText) findViewById(R.id.addCourse_CoursePriceET);
        fourthSectionToolbar = (Toolbar) findViewById(R.id.fourthSectionToolbar);

        setupToolbar();
        addData();
        setupRecyclerView();
        getIntentData();

        nextButton.setOnClickListener(this);
    }

   @RequiresApi(api = Build.VERSION_CODES.M)
    private void setupToolbar(){
        setSupportActionBar(fourthSectionToolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getIntentData(){
        courseName = getIntent().getStringExtra("courseName");
        courseCategory = getIntent().getStringExtra("courseCategory");
        courseSubcategory = getIntent().getStringExtra("courseSubcategory");
        courseSummary = getIntent().getStringExtra("courseSummary");
        courseStartDate = getIntent().getStringExtra("courseStartDate");
        courseEndDate = getIntent().getStringExtra("courseEndDate");
        courseScheduleArray = getIntent().getStringArrayExtra("courseScheduleArray");
        courseTimeArrayList = getIntent().getParcelableArrayListExtra("courseTimeArrayList");
        courseBenefitArrayList = getIntent().getParcelableArrayListExtra("courseBenefitArrayList");
        courseRequirementArrayList = getIntent().getParcelableArrayListExtra("courseRequirementArrayList");
        courseCurriculumArrayList = getIntent().getParcelableArrayListExtra("courseCurriculumArrayList");
    }

    private void setupRecyclerView(){
        courseTermsAdapter = new CourseTermsAdapter(termsArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        termsRecyclerView.setLayoutManager(layoutManager);
        termsRecyclerView.setAdapter(courseTermsAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addCourse_NextButton) {
            validate();
        } else {
            Toast.makeText(this, "nothing happened", Toast.LENGTH_SHORT).show();
        }
    }

    private void validate(){
        if (priceET.getText().toString().isEmpty()){
            priceET.setError("Price shouldn't be empty");
        }else if (!termsCheckbox.isChecked()){
            Toast.makeText(this, "Please read and agree with the terms and conditions.", Toast.LENGTH_SHORT).show();
        }else{
            saveData();
        }
    }

    private void saveData(){
        Intent intent = new Intent(this, AddFifthSectionActivity.class);

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
        intent.putExtra("coursePrice", priceET.getText().toString());

        startActivity(intent);
    }

    private void addData(){
        termsArrayList.add(new Terms("You will be given 3 absent quota for each student. If you pass the given quota, your course will be deleted and the money will be refunded."));
        termsArrayList.add(new Terms("You can cancel your student’s subscription anytime but you have to give the exact and valid reason of your cancellation."));
        termsArrayList.add(new Terms("A notification will appear at the time the course is going to start. If you exceed 15 minutes after the course started then it will be counted as absent."));
    }

}
