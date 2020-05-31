package com.example.learnbit.launch.student.home.category.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.model.coursedata.Course;
import com.example.learnbit.launch.student.home.coursedetails.StudentCourseDetailActivity;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class SubcategoryAdapter extends RecyclerView.Adapter<SubcategoryAdapter.SubcategoryViewHolder> {

    private ArrayList<Course> courseArrayList;
    private String key;

    public SubcategoryAdapter(ArrayList<Course> courseArrayList, String key) {
        this.courseArrayList = courseArrayList;
        this.key = key;
    }

    @NonNull
    @Override
    public SubcategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_course_card, parent, false);

        return new SubcategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubcategoryViewHolder holder, int position) {
        holder.courseName.setText(courseArrayList.get(position).getCourseName());
        holder.courseRating.setRating(courseArrayList.get(position).getCourseRating());
        holder.coursePrice.setText("IDR " + courseArrayList.get(position).getCoursePrice());

        Glide.with(holder.itemView.getContext()).load(courseArrayList.get(position).getCourseImageURL()).into(holder.courseImage);

        holder.courseCard.setOnClickListener(v -> {
            if (v.getId() == R.id.courseCardView){
                Intent intent = new Intent(holder.context, StudentCourseDetailActivity.class);
                intent.putExtra("key", key);
                intent.putExtra("courseName", holder.courseName.getText().toString());
                holder.context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (courseArrayList == null) ? 0 : courseArrayList.size();
    }

    public class SubcategoryViewHolder extends RecyclerView.ViewHolder{

        private ImageView courseImage;
        private TextView courseName;
        private RatingBar courseRating;
        private TextView coursePrice;
        private Context context;
        private CardView courseCard;

        public SubcategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            courseImage = itemView.findViewById(R.id.courseImageView);
            courseName = itemView.findViewById(R.id.courseName);
            courseRating = itemView.findViewById(R.id.courseRating);
            coursePrice = itemView.findViewById(R.id.coursePrice);
            courseCard = itemView.findViewById(R.id.courseCardView);

            context = itemView.getContext();
        }
    }
}
