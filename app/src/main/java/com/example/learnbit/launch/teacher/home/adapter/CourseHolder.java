package com.example.learnbit.launch.teacher.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.teacher.home.coursedetail.CourseDetailActivity;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CourseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView teacherCourseImageView;
    private TextView teacherCourseName, teacherCourseStudentCount, teacherCourseTime;
    private Context context;

    private String courseName;

    public CourseHolder(@NonNull View itemView) {
        super(itemView);

        teacherCourseImageView = (ImageView) itemView.findViewById(R.id.teacherCourseImageView);
        teacherCourseName = (TextView) itemView.findViewById(R.id.teacherCourseNameTV);
        teacherCourseStudentCount = (TextView) itemView.findViewById(R.id.teacherCourseStudentCountTV);
        teacherCourseTime = (TextView) itemView.findViewById(R.id.teacherCourseTimeTV);

        itemView.setClickable(true);
        itemView.setOnClickListener(this);
        teacherCourseImageView.setClipToOutline(true);

        context = itemView.getContext();
    }

    public void setCourse(Course course){
        courseName = course.getCourseName();
        String courseStudent = String.valueOf(course.getCourseStudent());
        String courseImageURL = course.getCourseImageURL();
        String courseAcceptance = course.getCourseAcceptance();
        HashMap<String, Boolean> courseTime = course.getCourseTime();

        String key = "";
        Boolean value = false;

        Log.d("curriculum", course.getCourseCurriculum() + " ");

        for (HashMap.Entry<String,Boolean> entry : courseTime.entrySet()) {
            key = entry.getKey();
            value = entry.getValue();
            Log.d("courseTimefirebase", key + value + " ");

            if (courseAcceptance.equals("pending")){
                teacherCourseTime.setText("Awaiting Admin Confirmation");
            }else {
                teacherCourseTime.setText("Course starts at " + key);
            }
        }

        teacherCourseName.setText(courseName);
        teacherCourseStudentCount.setText(courseStudent + " Student(s)");

        Glide.with(context).load(courseImageURL).into(teacherCourseImageView);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, CourseDetailActivity.class);
        intent.putExtra("courseName", courseName);
        context.startActivity(intent);
    }
}
