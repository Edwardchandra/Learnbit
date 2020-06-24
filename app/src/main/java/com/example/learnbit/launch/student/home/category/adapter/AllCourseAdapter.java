package com.example.learnbit.launch.student.home.category.adapter;

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

import static android.content.Context.MODE_PRIVATE;

public class AllCourseAdapter extends RecyclerView.Adapter<AllCourseAdapter.AllCourseViewHolder> {

    private ArrayList<Course> courseArrayList;

    public AllCourseAdapter(ArrayList<Course> courseArrayList) {
        this.courseArrayList = courseArrayList;
    }

    @NonNull
    @Override
    public AllCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_student_search, parent, false);

        return new AllCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllCourseViewHolder holder, int position) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (courseArrayList.get(position).getCourseStudent()!=null){
            HashMap<String, String> courseStudent =  courseArrayList.get(position).getCourseStudent();

            for (HashMap.Entry<String, String> entry : courseStudent.entrySet()){
                String value = entry.getValue();

                if (user!=null){
                    if (value.equals(user.getUid())){
                        holder.allCoursePrice.setText(holder.context.getString(R.string.course_applied));
                    }else{
                        holder.allCoursePrice.setText(holder.context.getString(R.string.price, courseArrayList.get(position).getCoursePrice()));
                    }
                }
            }
        }else{
            holder.allCoursePrice.setText(holder.context.getString(R.string.price, courseArrayList.get(position).getCoursePrice()));
        }

        holder.allCourseName.setText(courseArrayList.get(position).getCourseName());
        Glide.with(holder.context).load(courseArrayList.get(position).getCourseImageURL()).into(holder.allCourseImageView);
        holder.allCourseRatingBar.setRating(courseArrayList.get(position).getCourseRating());

        holder.key = courseArrayList.get(position).getCourseKey();
    }

    @Override
    public int getItemCount() {
        return (courseArrayList == null) ? 0 : courseArrayList.size();
    }

    public static class AllCourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView allCourseImageView;
        private TextView allCourseName, allCoursePrice;
        private RatingBar allCourseRatingBar;
        private Context context;
        private static final String detailPreference = "DETAIL_PREFERENCE";
        private String key;

        public AllCourseViewHolder(@NonNull View itemView) {
            super(itemView);

            allCourseImageView = itemView.findViewById(R.id.studentSearchImageView);
            allCourseName = itemView.findViewById(R.id.studentSearchCourseName);
            allCoursePrice = itemView.findViewById(R.id.studentSearchPrice);
            allCourseRatingBar = itemView.findViewById(R.id.studentSearchRatingBar);

            itemView.setOnClickListener(this);
            allCourseImageView.setClipToOutline(true);

            context = itemView.getContext();
        }

        @Override
        public void onClick(View v) {
            if (allCoursePrice.getText().toString().equals(context.getString(R.string.course_applied))){
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
