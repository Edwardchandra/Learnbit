package com.example.learnbit.launch.student.home.search.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.student.home.coursedetails.StudentCourseDetailActivity;
import com.example.learnbit.launch.teacher.home.coursedetail.CourseDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StudentSearchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView studentSearchImageView;
    private TextView studentSearchCourseName, studentSearchCoursePrice;
    private RatingBar studentSearchRatingBar;
    private Context context;

    private String key;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    public StudentSearchHolder(@NonNull View itemView) {
        super(itemView);

        studentSearchImageView = itemView.findViewById(R.id.studentSearchImageView);
        studentSearchCourseName = itemView.findViewById(R.id.studentSearchCourseName);
        studentSearchCoursePrice = itemView.findViewById(R.id.studentSearchPrice);
        studentSearchRatingBar = itemView.findViewById(R.id.studentSearchRatingBar);

        itemView.setClickable(true);
        itemView.setOnClickListener(this);
        studentSearchImageView.setClipToOutline(true);

        context = itemView.getContext();

        setupFirebase();
    }

    private void setupFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    public void setCourse(String key, Course course){
        this.key = key;

        if (course!=null){
            studentSearchCourseName.setText(course.getCourseName());
            studentSearchRatingBar.setRating(course.getCourseRating());

            Glide.with(context).load(course.getCourseImageURL()).into(studentSearchImageView);

            if (course.getCourseStudent()!=null){
                HashMap<String, String> courseStudent =  course.getCourseStudent();

                for (HashMap.Entry<String, String> entry : courseStudent.entrySet()){
                    String value = entry.getValue();

                    if (value.equals(user.getUid())){
                        studentSearchCoursePrice.setText(context.getString(R.string.course_applied));
                    }else{
                        studentSearchCoursePrice.setText(context.getString(R.string.price, course.getCoursePrice()));
                    }
                }
            }else{
                studentSearchCoursePrice.setText(context.getString(R.string.price, course.getCoursePrice()));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (studentSearchCoursePrice.getText().toString().equals(context.getString(R.string.course_applied))){
            Toast.makeText(context, "you have applied to this course.", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(context, StudentCourseDetailActivity.class);
            intent.putExtra("key", key);
            intent.putExtra("courseName", studentSearchCourseName.getText().toString());
            context.startActivity(intent);
        }
    }
}
