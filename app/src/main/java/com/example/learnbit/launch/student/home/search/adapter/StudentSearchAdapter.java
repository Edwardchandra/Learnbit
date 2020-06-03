package com.example.learnbit.launch.student.home.search.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.student.course.mycoursedetail.MyCourseDetailActivity;
import com.example.learnbit.launch.student.home.coursedetails.StudentCourseDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;

public class StudentSearchAdapter extends RecyclerView.Adapter<StudentSearchAdapter.StudentSearchViewHolder> {

    private ArrayList<Course> courseArrayList;
    private ArrayList<String> keyArrayList;

    public StudentSearchAdapter(ArrayList<Course> courseArrayList, ArrayList<String> keyArrayList) {
        this.courseArrayList = courseArrayList;
        this.keyArrayList = keyArrayList;
    }

    @NonNull
    @Override
    public StudentSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_student_search, parent, false);

        return new StudentSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentSearchViewHolder holder, int position) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (courseArrayList.get(position).getCourseStudent()!=null){
            HashMap<String, String> courseStudent =  courseArrayList.get(position).getCourseStudent();

            for (HashMap.Entry<String, String> entry : courseStudent.entrySet()){
                String value = entry.getValue();

                if (value.equals(user.getUid())){
                    holder.studentSearchCoursePrice.setText(holder.context.getString(R.string.course_applied));
                }else{
                    holder.studentSearchCoursePrice.setText(holder.context.getString(R.string.price, courseArrayList.get(position).getCoursePrice()));
                }
            }
        }else{
            holder.studentSearchCoursePrice.setText(holder.context.getString(R.string.price, courseArrayList.get(position).getCoursePrice()));
        }

        holder.studentSearchCourseName.setText(courseArrayList.get(position).getCourseName());
        Glide.with(holder.context).load(courseArrayList.get(position).getCourseImageURL()).into(holder.studentSearchImageView);
        holder.studentSearchRatingBar.setRating(courseArrayList.get(position).getCourseRating());

        holder.key = keyArrayList.get(position);
    }

    @Override
    public int getItemCount() {
        return (courseArrayList == null || keyArrayList == null) ? 0 : courseArrayList.size();
    }

    public void updateList(ArrayList<Course> courseArrayList){
        this.courseArrayList = courseArrayList;
        notifyDataSetChanged();
    }

    public static class StudentSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView studentSearchImageView;
        private TextView studentSearchCourseName, studentSearchCoursePrice;
        private RatingBar studentSearchRatingBar;
        private Context context;

        private String key;

        public StudentSearchViewHolder(@NonNull View itemView) {
            super(itemView);

            studentSearchImageView = itemView.findViewById(R.id.studentSearchImageView);
            studentSearchCourseName = itemView.findViewById(R.id.studentSearchCourseName);
            studentSearchCoursePrice = itemView.findViewById(R.id.studentSearchPrice);
            studentSearchRatingBar = itemView.findViewById(R.id.studentSearchRatingBar);

            itemView.setClickable(true);
            itemView.setOnClickListener(this);
            studentSearchImageView.setClipToOutline(true);

            context = itemView.getContext();
        }

        @Override
        public void onClick(View v) {
            if (studentSearchCoursePrice.getText().toString().equals(context.getString(R.string.course_applied))){
                Intent intent = new Intent(context, MyCourseDetailActivity.class);
                intent.putExtra("courseName", studentSearchCourseName.getText().toString());
                intent.putExtra("key", key);
                context.startActivity(intent);
            }else{
                Intent intent = new Intent(context, StudentCourseDetailActivity.class);
                intent.putExtra("key", key);
                intent.putExtra("courseName", studentSearchCourseName.getText().toString());
                context.startActivity(intent);
            }
        }
    }
}
