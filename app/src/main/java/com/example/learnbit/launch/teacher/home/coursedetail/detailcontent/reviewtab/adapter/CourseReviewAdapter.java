package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.reviewtab.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.reviewtab.model.CourseReview;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CourseReviewAdapter extends RecyclerView.Adapter<CourseReviewAdapter.CourseReviewViewHolder> {

    //variables
    private ArrayList<CourseReview> courseReviewArrayList;

    //constructor
    public CourseReviewAdapter(ArrayList<CourseReview> courseReviewArrayList) {
        this.courseReviewArrayList = courseReviewArrayList;
    }

    //inflate from layout
    @NonNull
    @Override
    public CourseReviewAdapter.CourseReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_teacher_course_reviews, parent, false);

        return new CourseReviewViewHolder(view);
    }

    //set data
    @Override
    public void onBindViewHolder(@NonNull CourseReviewAdapter.CourseReviewViewHolder holder, int position) {
        holder.reviewImage.setClipToOutline(true);

        FirebaseDatabase.getInstance().getReference("Users").child(courseReviewArrayList.get(position).getUser()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                if (name!=null){
                    holder.reviewName.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                holder.toast(holder.context.getString(R.string.retrieve_failed));
            }
        });

        FirebaseStorage.getInstance().getReference("Users").child(courseReviewArrayList.get(position).getUser()).child("profileimage").getDownloadUrl()
                .addOnSuccessListener(uri -> Glide.with(holder.context).load(uri).into(holder.reviewImage))
                .addOnFailureListener(e -> holder.toast(holder.context.getString(R.string.retrieve_failed)));

        holder.reviewDate.setText(courseReviewArrayList.get(position).getTime());
        holder.reviewContent.setText(courseReviewArrayList.get(position).getMessage());
        holder.reviewRating.setRating(courseReviewArrayList.get(position).getRating());
    }

    //set cell size
    @Override
    public int getItemCount() {
        return courseReviewArrayList.size();
    }

    //recyclerview view holder class
    public static class CourseReviewViewHolder extends RecyclerView.ViewHolder{

        //elements variable
        private ImageView reviewImage;
        private TextView reviewName, reviewDate, reviewContent;
        private RatingBar reviewRating;

        //variable
        private Context context;

        //constructor
        public CourseReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            reviewImage = itemView.findViewById(R.id.teacherCourse_ReviewImage);
            reviewName = itemView.findViewById(R.id.teacherCourse_ReviewName);
            reviewDate = itemView.findViewById(R.id.teacherCourse_ReviewDate);
            reviewContent = itemView.findViewById(R.id.teacherCourse_ReviewContent);
            reviewRating = itemView.findViewById(R.id.teacherCourse_ReviewRating);

            context = itemView.getContext();
        }

        //show toast
        private void toast(String message){
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
