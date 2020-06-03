package com.example.learnbit.launch.student.course.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.model.userdata.student.StudentCourse;
import com.example.learnbit.launch.student.course.mycoursedetail.MyCourseDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MyCourseAdapter extends RecyclerView.Adapter<MyCourseAdapter.MyCourseViewHolder> {

    private ArrayList<StudentCourse> courseArrayList;

    public MyCourseAdapter(ArrayList<StudentCourse> courseArrayList) {
        this.courseArrayList = courseArrayList;
    }

    @NonNull
    @Override
    public MyCourseAdapter.MyCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_student_course, parent, false);

        return new MyCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCourseAdapter.MyCourseViewHolder holder, int position) {
        holder.courseName.setText(courseArrayList.get(position).getCourseName());

        FirebaseDatabase.getInstance().getReference("Users").child(courseArrayList.get(position).getTeacherUID()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String teacherName = dataSnapshot.getValue(String.class);

                if (teacherName!=null) holder.courseTeacher.setText(teacherName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(holder.itemView.getContext(), "Failed to retrieve teacher data", Toast.LENGTH_SHORT).show();
            }
        });

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        String today = simpleDateFormat.format(Calendar.getInstance().getTime());

        for (HashMap.Entry<String, String> entry : courseArrayList.get(position).getCourseSchedule().entrySet()){
            String schedule = entry.getValue();

            if (schedule.equals(today)){
                holder.courseSchedule.setText("Course starts at " + courseArrayList.get(position).getCourseTime());
            }else{
                holder.courseSchedule.setText("You have no course for today");
            }
        }

        Glide.with(holder.itemView.getContext()).load(courseArrayList.get(position).getCourseImageURL()).into(holder.courseImageView);

        holder.itemView.setOnClickListener((v) -> {
            Intent intent = new Intent(holder.itemView.getContext(), MyCourseDetailActivity.class);
            intent.putExtra("courseName", courseArrayList.get(position).getCourseName());
            intent.putExtra("key", courseArrayList.get(position).getTeacherUID());
            holder.itemView.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return (courseArrayList == null) ? 0 : courseArrayList.size();
    }

    public static class MyCourseViewHolder extends RecyclerView.ViewHolder {

        private ImageView courseImageView;
        private TextView courseName, courseTeacher, courseSchedule;

        public MyCourseViewHolder(@NonNull View itemView) {
            super(itemView);

            courseImageView = itemView.findViewById(R.id.myCourseImage);
            courseName = itemView.findViewById(R.id.myCourseName);
            courseTeacher = itemView.findViewById(R.id.myCourseTeacher);
            courseSchedule = itemView.findViewById(R.id.myCourseSchedule);

            courseImageView.setClipToOutline(true);
        }
    }
}
