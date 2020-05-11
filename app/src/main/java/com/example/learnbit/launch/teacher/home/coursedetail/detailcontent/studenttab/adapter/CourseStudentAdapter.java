package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.studenttab.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.studenttab.model.CourseStudent;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CourseStudentAdapter extends RecyclerView.Adapter<CourseStudentAdapter.CourseStudentViewHolder> {

    private ArrayList<CourseStudent> courseStudentArrayList;

    public CourseStudentAdapter(ArrayList<CourseStudent> courseStudentArrayList) {
        this.courseStudentArrayList = courseStudentArrayList;
    }

    @NonNull
    @Override
    public CourseStudentAdapter.CourseStudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.teacher_course_detail_student, parent, false);

        return new CourseStudentAdapter.CourseStudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseStudentAdapter.CourseStudentViewHolder holder, int position) {
        holder.studentImageView.setImageResource(courseStudentArrayList.get(position).getStudentImage());
        holder.studentName.setText(courseStudentArrayList.get(position).getStudentName());
        holder.studentStatus.setText(courseStudentArrayList.get(position).getStudentStatus());
        holder.studentTime.setText(courseStudentArrayList.get(position).getStudentTime());
    }

    @Override
    public int getItemCount() {
        return (courseStudentArrayList != null) ? courseStudentArrayList.size() : 0;
    }

    public class CourseStudentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView studentImageView;
        private TextView studentName, studentStatus, studentTime;
        private final Context context;

        public CourseStudentViewHolder(@NonNull View itemView) {
            super(itemView);

            studentImageView = (ImageView) itemView.findViewById(R.id.teacherCourse_StudentImage);
            studentName = (TextView) itemView.findViewById(R.id.teacherCourse_StudentName);
            studentStatus = (TextView) itemView.findViewById(R.id.teacherCourse_StudentStatus);
            studentTime = (TextView) itemView.findViewById(R.id.teacherCourse_StudentTime);

            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            studentImageView.setClipToOutline(true);

            context = itemView.getContext();
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(context, "Clicked u mf", Toast.LENGTH_SHORT).show();
        }
    }
}
