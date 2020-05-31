package com.example.learnbit.launch.student.home.adapter;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CourseCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView courseImage;
    private TextView courseName;
    private RatingBar courseRating;
    private TextView coursePrice;
    private Context context;
    private CardView courseCard;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private String key;

    public CourseCardHolder(@NonNull View itemView) {
        super(itemView);

        courseImage = itemView.findViewById(R.id.courseImageView);
        courseName = itemView.findViewById(R.id.courseName);
        courseRating = itemView.findViewById(R.id.courseRating);
        coursePrice = itemView.findViewById(R.id.coursePrice);
        courseCard = itemView.findViewById(R.id.courseCardView);

        courseCard.setOnClickListener(this);

        context = itemView.getContext();

        setupFirebase();
    }

    private void setupFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    public void setCourse(String key){
        this.key = key;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Course").child(key);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Course course = ds.getValue(Course.class);

                    if (course!=null){
                        courseName.setText(course.getCourseName());
                        courseRating.setRating(course.getCourseRating());

                        Glide.with(context).load(course.getCourseImageURL()).into(courseImage);

                        if (course.getCourseStudent()!=null){
                            HashMap<String, String> courseStudent =  course.getCourseStudent();

                            for (HashMap.Entry<String, String> entry : courseStudent.entrySet()){
                                String value = entry.getValue();

                                if (value.equals(user.getUid())){
                                    coursePrice.setText(context.getString(R.string.course_applied));
                                }else{
                                    coursePrice.setText(context.getString(R.string.price, course.getCoursePrice()));
                                }
                            }
                        }else{
                            coursePrice.setText(context.getString(R.string.price, course.getCoursePrice()));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Failed to fetch database data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (coursePrice.getText().toString().equals(context.getString(R.string.course_applied))){
            Toast.makeText(context, "you have applied to this course.", Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(context, StudentCourseDetailActivity.class);
            intent.putExtra("key", key);
            intent.putExtra("courseName", courseName.getText().toString());
            context.startActivity(intent);
        }
    }
}
