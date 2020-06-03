package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.studenttab.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.studenttab.model.CourseStudent;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        View view = layoutInflater.inflate(R.layout.item_student_list, parent, false);

        return new CourseStudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseStudentAdapter.CourseStudentViewHolder holder, int position) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(courseStudentArrayList.get(position).getStudentUID());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Users").child(courseStudentArrayList.get(position).getStudentUID());

        databaseReference.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);

                if (name!=null) holder.studentName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(holder.context, "failed to retrieve student name", Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference.child("student").child("courses").child(courseStudentArrayList.get(position).getCourseKey()).child("courseTime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String time = dataSnapshot.getValue(String.class);

                if (time!=null) holder.studentTime.setText("Course will start at " + time);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(holder.context, "failed to retrieve course time", Toast.LENGTH_SHORT).show();
            }
        });

        storageReference.child("profileimage").getDownloadUrl().addOnSuccessListener(uri -> Glide.with(holder.context).load(uri).into(holder.studentImageView));
    }

    @Override
    public int getItemCount() {
        return (courseStudentArrayList != null) ? courseStudentArrayList.size() : 0;
    }

    public static class CourseStudentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView studentImageView;
        private TextView studentName, studentTime;
        private final Context context;

        public CourseStudentViewHolder(@NonNull View itemView) {
            super(itemView);

            studentImageView = (ImageView) itemView.findViewById(R.id.teacherCourse_StudentImage);
            studentName = (TextView) itemView.findViewById(R.id.teacherCourse_StudentName);
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
