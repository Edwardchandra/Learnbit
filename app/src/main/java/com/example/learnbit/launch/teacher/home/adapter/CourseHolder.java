package com.example.learnbit.launch.teacher.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.teacher.home.coursedetail.CourseDetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.MODE_PRIVATE;

public class CourseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    //initiate elements variable
    private ImageView teacherCourseImageView;
    private TextView teacherCourseName, teacherCourseStudentCount, teacherCourseTime;

    //initiate variable
    private String courseName;
    private Context context;
    private String key;

    private static final String detailPreference = "DETAIL_PREFERENCE";

    //constructor
    public CourseHolder(@NonNull View itemView) {
        super(itemView);

        //connect elements variable to layout file elements
        teacherCourseImageView = itemView.findViewById(R.id.teacherCourseImageView);
        teacherCourseName = itemView.findViewById(R.id.teacherCourseNameTV);
        teacherCourseStudentCount = itemView.findViewById(R.id.teacherCourseStudentCountTV);
        teacherCourseTime = itemView.findViewById(R.id.teacherCourseTimeTV);

        //set itemView clickable and set itemView click listener to override method
        itemView.setClickable(true);
        itemView.setOnClickListener(this);

        //set imageview corner to rounded corner
        teacherCourseImageView.setClipToOutline(true);

        //get context
        context = itemView.getContext();
    }

    //apply course information to cell
    //receive Course Object parameter
    public void setCourse(Course course, String key){
        if (course!=null){
            //set global variable key to key parameter value
            this.key = key;

            //get course name from course object
            courseName = course.getCourseName();

            //get current time
            Date currentTime = Calendar.getInstance().getTime();

            //initiate new date
            Date startDate = new Date();
            Date endDate = new Date();

            //date formatter
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);

            //simple date format is used to format date to the desired format
            //this formatter convert date to "HOUR:MINUTES [AM/PM]" format
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);

            for (HashMap.Entry<String, String> entry : course.getCourseDate().entrySet()){
                if (entry.getKey().equals("startDate")){
                    try {
                        startDate = simpleDateFormat1.parse(entry.getValue());
                    }catch (ParseException e){
                        e.printStackTrace();
                    }
                }else if (entry.getKey().equals("endDate")){
                    try {
                        endDate = simpleDateFormat1.parse(entry.getValue());
                    }catch (ParseException e){
                        e.printStackTrace();
                    }
                }
            }

            //check if course is accepted/not
            //if course is still pending, set course time textfield to "awaiting admin confirmation"
            //if course is accepted, show next schedule for today
            if (course.getCourseAcceptance().equals("pending")){
                teacherCourseTime.setText(context.getString(R.string.awaiting_admin_confirmation));
            }else {
                if (currentTime.after(startDate) && currentTime.before(endDate)){
                    //loop through course time for next course time
                    for (HashMap.Entry<String, Boolean> entry : course.getCourseTime().entrySet()){

                        //check if course time is being taken
                        //if no time is taken, set to no schedule today
                        if (entry.getValue()) {
                            //initiate current date
                            Date courseTime = new Date();

                            //parse current date using date formatter
                            //catch error if parse failed
                            try {
                                courseTime = simpleDateFormat.parse(entry.getKey());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            //check if current time is not yet passing course time
                            //if not yet pass the course time, then set to course time textfield
                            //if passed then see next course time, if no course time ahead then set to no schedule today
                            if (!currentTime.after(courseTime)) {
                                teacherCourseTime.setText(context.getString(R.string.course_start_time, entry.getKey()));
                            } else {
                                teacherCourseTime.setText(context.getString(R.string.no_course));
                            }
                        }else{
                            teacherCourseTime.setText(context.getString(R.string.no_course));
                        }
                    }
                }else if (!currentTime.after(startDate) && currentTime.before(endDate)){
                    teacherCourseTime.setText(context.getString(R.string.course_start_period));
                }else if (currentTime.after(startDate) && !currentTime.before(endDate)){
                    teacherCourseTime.setText(context.getString(R.string.course_end_period));
                }
            }

            //if course student is not null, set course student count field to total applied student
            //if null, set to no student yet
            if (course.getCourseStudent() != null){
                teacherCourseStudentCount.setText(context.getString(R.string.course_student_count, course.getCourseStudent().size()));
            }else{
                teacherCourseStudentCount.setText(context.getString(R.string.no_course_student));
            }

            //set course name
            teacherCourseName.setText(courseName);

            //set course image
            Glide.with(context).load(course.getCourseImageURL()).into(teacherCourseImageView);
        }
    }

    //cell onclick executed here
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, CourseDetailActivity.class);
        intent.putExtra("courseName", courseName);
        intent.putExtra("key", key);
        savePreferenceData();
        context.startActivity(intent);
    }

    private void savePreferenceData(){
        SharedPreferences preferences = context.getSharedPreferences(detailPreference, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("courseKey", key);
        editor.apply();
    }
}
