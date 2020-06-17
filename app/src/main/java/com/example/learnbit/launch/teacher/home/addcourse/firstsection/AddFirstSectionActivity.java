package com.example.learnbit.launch.teacher.home.addcourse.firstsection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.addcourse.secondsection.AddSecondSectionActivity;

public class AddFirstSectionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private Spinner courseSubcategorySpinner;
    private EditText courseNameET, courseSummaryET;
    private Toolbar firstSectionToolbar;

    private String spinnerValue = "";
    private String subSpinnerValue = "";

    private ArrayAdapter<CharSequence> categoryAdapter;
    private ArrayAdapter<CharSequence> languageAdapter;
    private ArrayAdapter<CharSequence> personalDevAdapter;
    private ArrayAdapter<CharSequence> computerAdapter;
    private ArrayAdapter<CharSequence> mathAdapter;
    private ArrayAdapter<CharSequence> naturalAdapter;
    private ArrayAdapter<CharSequence> socialAdapter;
    private ArrayAdapter<CharSequence> artAdapter;
    private ArrayAdapter<CharSequence> civicAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_first_section);

        Spinner courseCategorySpinner = findViewById(R.id.addCourse_CourseCategory);
        courseSubcategorySpinner = findViewById(R.id.addCourse_CourseSubcategory);
        courseNameET = findViewById(R.id.addCourse_CourseNameET);
        courseSummaryET = findViewById(R.id.addCourse_CourseSummaryET);
        Button courseNextButton = findViewById(R.id.addCourse_NextButton);
        firstSectionToolbar = findViewById(R.id.firstSectionToolbar);

        courseNextButton.setOnClickListener(this);

        setupSpinner(categoryAdapter, R.array.category_array, courseCategorySpinner);
        setupToolbar();
    }

    private void setupToolbar(){
        setSupportActionBar(firstSectionToolbar);

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

    private void setupSpinner(ArrayAdapter<CharSequence> arrayAdapter, int textArray, Spinner spinner){
        arrayAdapter = ArrayAdapter.createFromResource(this, textArray, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void subSpinnerSetup(){
        switch (spinnerValue){
            case "Language and Literature":
                setupSpinner(languageAdapter, R.array.language_array, courseSubcategorySpinner);
                Toast.makeText(this, spinnerValue, Toast.LENGTH_SHORT).show();
                break;
            case "Personal Development":
                setupSpinner(personalDevAdapter, R.array.personal_development_array, courseSubcategorySpinner);
                break;
            case "Computer Technology":
                setupSpinner(computerAdapter, R.array.computer_array, courseSubcategorySpinner);
                break;
            case "Mathematics and Logic":
                setupSpinner(mathAdapter, R.array.mathematics_array, courseSubcategorySpinner);
                break;
            case "Natural Science":
                setupSpinner(naturalAdapter, R.array.natural_array, courseSubcategorySpinner);
                break;
            case "Social Science":
                setupSpinner(socialAdapter, R.array.social_array, courseSubcategorySpinner);
                break;
            case "Art and Culture":
                setupSpinner(artAdapter, R.array.art_array, courseSubcategorySpinner);
                break;
            case "Civic Education":
                setupSpinner(civicAdapter, R.array.civic_array, courseSubcategorySpinner);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;

        switch (spinner.getId()){
            case R.id.addCourse_CourseCategory:
                spinnerValue = parent.getItemAtPosition(position).toString();
                TextView selectedValue = (TextView) parent.getChildAt(0);
                if (selectedValue!=null){
                    selectedValue.setTextColor(getResources().getColor(android.R.color.black));
                }
                subSpinnerSetup();
            case R.id.addCourse_CourseSubcategory:
                subSpinnerValue = parent.getItemAtPosition(position).toString();
                TextView subselectedvalue = (TextView) parent.getChildAt(0);
                if (subselectedvalue!=null){
                    subselectedvalue.setTextColor(getResources().getColor(android.R.color.black));
                }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkEditText(){
        if (courseNameET.getText().toString().isEmpty()){
            courseNameET.setError("Course name shouldn't be empty");
        }else if (courseSummaryET.getText().toString().isEmpty()){
            courseSummaryET.setError("Course summary shouldn't be empty");
        }else{
            Intent intent = new Intent(getApplicationContext(), AddSecondSectionActivity.class);
            intent.putExtra("courseName", courseNameET.getText().toString());
            intent.putExtra("courseCategory", spinnerValue);
            intent.putExtra("courseSubcategory", subSpinnerValue);
            intent.putExtra("courseSummary", courseSummaryET.getText().toString());
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addCourse_NextButton) {
            checkEditText();
        }
    }
}
