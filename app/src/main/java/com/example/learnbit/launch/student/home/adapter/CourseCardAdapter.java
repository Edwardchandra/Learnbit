package com.example.learnbit.launch.student.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.student.course.mycoursedetail.MyCourseDetailActivity;
import com.example.learnbit.launch.student.home.StudentHomeFragment;
import com.example.learnbit.launch.student.home.coursedetails.StudentCourseDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class CourseCardAdapter extends RecyclerView.Adapter<CourseCardAdapter.CourseCardViewHolder> {

    private ArrayList<Course> courseArrayList;

    public CourseCardAdapter(ArrayList<Course> courseArrayList) {
        this.courseArrayList = courseArrayList;
    }

    @NonNull
    @Override
    public CourseCardAdapter.CourseCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_course_card, parent, false);

        return new CourseCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseCardAdapter.CourseCardViewHolder holder, int position) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (courseArrayList.get(position).getCourseStudent()!=null){
            HashMap<String, String> courseStudent =  courseArrayList.get(position).getCourseStudent();

            for (HashMap.Entry<String, String> entry : courseStudent.entrySet()){
                String value = entry.getValue();

                if (user!=null){
                    if (value.equals(user.getUid())){
                        holder.coursePrice.setText(holder.context.getString(R.string.course_applied));
                    }else{
                        holder.coursePrice.setText(holder.context.getString(R.string.price, courseArrayList.get(position).getCoursePrice()));
                    }
                }
            }
        }else{
            holder.coursePrice.setText(holder.context.getString(R.string.price, courseArrayList.get(position).getCoursePrice()));
        }

        holder.courseName.setText(courseArrayList.get(position).getCourseName());
        Glide.with(holder.context).load(courseArrayList.get(position).getCourseImageURL()).into(holder.courseImage);
        holder.courseRating.setRating(courseArrayList.get(position).getCourseRating());

        holder.key = courseArrayList.get(position).getCourseKey();
    }

    @Override
    public int getItemCount() {
        return (courseArrayList == null) ? 0 : courseArrayList.size();
    }

    public static class CourseCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView courseImage;
        private TextView courseName;
        private RatingBar courseRating;
        private TextView coursePrice;
        private Context context;
        private static final String detailPreference = "DETAIL_PREFERENCE";
        private String key;

        public CourseCardViewHolder(@NonNull View itemView) {
            super(itemView);

            courseImage = itemView.findViewById(R.id.courseImageView);
            courseName = itemView.findViewById(R.id.courseName);
            courseRating = itemView.findViewById(R.id.courseRating);
            coursePrice = itemView.findViewById(R.id.coursePrice);
            CardView courseCard = itemView.findViewById(R.id.courseCardView);

            courseCard.setOnClickListener(this);

            context = itemView.getContext();
        }

        @Override
        public void onClick(View v) {
            if (coursePrice.getText().toString().equals(context.getString(R.string.course_applied))){
                Intent intent = new Intent(context, MyCourseDetailActivity.class);
                intent.putExtra("key", key);
                context.startActivity(intent);
            }else{
                savePreferenceData();
                Intent intent = new Intent(context, StudentCourseDetailActivity.class);
                intent.putExtra("key", key);
                context.startActivity(intent);
            }
        }

        private void savePreferenceData(){
            SharedPreferences preferences = itemView.getContext().getSharedPreferences(detailPreference, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("courseKey", key);
            editor.apply();
        }
    }
}
