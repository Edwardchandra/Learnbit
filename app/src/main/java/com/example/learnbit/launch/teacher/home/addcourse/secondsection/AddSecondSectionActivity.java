package com.example.learnbit.launch.teacher.home.addcourse.secondsection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.addcourse.secondsection.adapter.CourseTimeAdapter;
import com.example.learnbit.launch.teacher.home.addcourse.secondsection.model.Time;
import com.example.learnbit.launch.teacher.home.addcourse.thirdsection.AddThirdSectionActivity;

import org.joda.time.DateTime;
import org.joda.time.Weeks;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AddSecondSectionActivity extends AppCompatActivity implements View.OnClickListener {

    private CheckBox monCheckbox, tuesCheckbox, wedCheckbox, thursCheckbox, friCheckbox, satCheckbox, sunCheckbox;
    private EditText startDateET, endDateET, activeET;
    private Toolbar secondSectionToolbar;

    private int[] scheduleCheckboxInt = {R.id.addCourse_MondayCheckbox,
                                        R.id.addCourse_TuesdayCheckbox,
                                        R.id.addCourse_WednesdayCheckbox,
                                        R.id.addCourse_ThursdayCheckbox,
                                        R.id.addCourse_FridayCheckbox,
                                        R.id.addCourse_SaturdayCheckbox,
                                        R.id.addCourse_SundayCheckbox};

    private CheckBox[] scheduleCheckbox = {monCheckbox, tuesCheckbox, wedCheckbox, thursCheckbox, friCheckbox, satCheckbox, sunCheckbox};

    private ArrayList<String> selectedCheckbox = new ArrayList<>();
    private String[] selectedCheckboxArray;

    private Calendar calendar;

    private CourseTimeAdapter courseTimeAdapter;
    private ArrayList<Time> courseTimeArrayList;

    private String courseName;
    private String courseCategory;
    private String courseSubcategory;
    private String courseSummary;

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate();
        }
    };

    public AddSecondSectionActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_second_section);

        startDateET = findViewById(R.id.addCourse_CourseStartDate);
        endDateET = findViewById(R.id.addCourse_CourseEndDate);
        Button nextButton = findViewById(R.id.addCourse_NextButton);
        Button addCourseTimeButton = findViewById(R.id.addCourse_AddNewCourseTimeButton);
        RecyclerView courseTimeRecyclerView = findViewById(R.id.addCourse_CourseTimeRecyclerView);
        secondSectionToolbar = findViewById(R.id.secondSectionToolbar);

        calendar = Calendar.getInstance();

        startDateET.setOnClickListener(this);
        endDateET.setOnClickListener(this);
        addCourseTimeButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        courseTimeAdapter = new CourseTimeAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        courseTimeRecyclerView.setLayoutManager(layoutManager);
        courseTimeRecyclerView.setAdapter(courseTimeAdapter);

        for (int i=0;i<7;i++){
            scheduleCheckbox[i] = findViewById(scheduleCheckboxInt[i]);
        }

        setupToolbar();
        getIntentData();
    }

    private void setupToolbar(){
        setSupportActionBar(secondSectionToolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            getWindow().setStatusBarColor(Color.WHITE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void getIntentData(){
        courseName = getIntent().getStringExtra("courseName");
        courseCategory = getIntent().getStringExtra("courseCategory");
        courseSubcategory = getIntent().getStringExtra("courseSubcategory");
        courseSummary = getIntent().getStringExtra("courseSummary");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addCourse_CourseStartDate:
                dateAction(v);
                break;
            case R.id.addCourse_CourseEndDate:
                dateAction(v);
                break;
            case R.id.addCourse_AddNewCourseTimeButton:
                Time time = new Time();
                courseTimeAdapter.addEditText(time);
                break;
            case R.id.addCourse_NextButton:
                nextButtonAction();
                getTimeArrayList();
                editTextCheck();
                break;
        }
    }

    private void nextButtonAction(){
        selectedCheckbox.clear();
        for (CheckBox checkbox : scheduleCheckbox) {
            if (checkbox.isChecked()) {
                selectedCheckbox.add(checkbox.getText().toString());
            }
        }

        selectedCheckboxArray = selectedCheckbox.toArray(new String[0]);
    }

    private void getTimeArrayList(){
        courseTimeArrayList = courseTimeAdapter.getArrayList();
    }

    private long getDateTimeMillisecond(String date){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yy", Locale.US);
            LocalDate localDate = LocalDate.parse(date, dateTimeFormatter);

            return localDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        }
        return 0;
    }

    private int getWeeks(long fromDate, long toDate){
        int weeks = Weeks.weeksBetween(new DateTime(fromDate), new DateTime(toDate)).getWeeks();
        if (weeks < 0) {
            weeks = Weeks.weeksBetween(new DateTime(toDate), new DateTime(fromDate)).getWeeks();
        }
        return weeks;
    }

    private void updateDate(){
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        activeET.setText(sdf.format(calendar.getTime()));
    }

    private void dateAction(View view){
        activeET = (EditText) view;
        new DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void editTextCheck(){
        String startDate = startDateET.getText().toString();
        String endDate = endDateET.getText().toString();
        int weeks = getWeeks(getDateTimeMillisecond(startDateET.getText().toString()), getDateTimeMillisecond(endDateET.getText().toString()));

        if (startDate.isEmpty()){
            startDateET.setError(getString(R.string.start_date_error));
        }else if (endDate.isEmpty()){
            endDateET.setError(getString(R.string.end_date_error));
        }else if(weeks < 2){
            Toast.makeText(this, getString(R.string.course_length_error), Toast.LENGTH_SHORT).show();
        }else if(courseTimeArrayList.size() == 0) {
            Toast.makeText(this, getString(R.string.time_schedule_error), Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(this, AddThirdSectionActivity.class);
            intent.putExtra("weekcount", weeks);
            intent.putExtra("courseName", courseName);
            intent.putExtra("courseCategory", courseCategory);
            intent.putExtra("courseSubcategory", courseSubcategory);
            intent.putExtra("courseSummary", courseSummary);
            intent.putExtra("courseStartDate", startDateET.getText().toString());
            intent.putExtra("courseEndDate", endDateET.getText().toString());
            intent.putExtra("courseScheduleArray", selectedCheckboxArray);
            intent.putParcelableArrayListExtra("courseTimeArrayList", courseTimeArrayList);

            startActivity(intent);
        }

    }
}
