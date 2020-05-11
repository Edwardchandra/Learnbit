package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.reviewtab.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.reviewtab.model.CourseReview;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CourseReviewAdapter extends RecyclerView.Adapter<CourseReviewAdapter.CourseReviewViewHolder> {

    private ArrayList<CourseReview> courseReviewArrayList;

    public CourseReviewAdapter(ArrayList<CourseReview> courseReviewArrayList) {
        this.courseReviewArrayList = courseReviewArrayList;
    }

    @NonNull
    @Override
    public CourseReviewAdapter.CourseReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.teacher_course_reviews, parent, false);

        return new CourseReviewAdapter.CourseReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseReviewAdapter.CourseReviewViewHolder holder, int position) {
        holder.reviewImage.setClipToOutline(true);
        holder.reviewImage.setImageResource(courseReviewArrayList.get(position).getReviewImage());
        holder.reviewName.setText(courseReviewArrayList.get(position).getReviewName());
        holder.reviewDate.setText(courseReviewArrayList.get(position).getReviewDate());
        holder.reviewContent.setText(courseReviewArrayList.get(position).getReviewText());
        holder.reviewRating.setRating(courseReviewArrayList.get(position).getReviewRating());
    }

    @Override
    public int getItemCount() {
        return courseReviewArrayList.size();
    }

    public class CourseReviewViewHolder extends RecyclerView.ViewHolder{

        private ImageView reviewImage;
        private TextView reviewName, reviewDate, reviewContent;
        private RatingBar reviewRating;

        public CourseReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            reviewImage = (ImageView) itemView.findViewById(R.id.teacherCourse_ReviewImage);
            reviewName = (TextView) itemView.findViewById(R.id.teacherCourse_ReviewName);
            reviewDate = (TextView) itemView.findViewById(R.id.teacherCourse_ReviewDate);
            reviewContent = (TextView) itemView.findViewById(R.id.teacherCourse_ReviewContent);
            reviewRating = (RatingBar) itemView.findViewById(R.id.teacherCourse_ReviewRating);
        }
    }
}
