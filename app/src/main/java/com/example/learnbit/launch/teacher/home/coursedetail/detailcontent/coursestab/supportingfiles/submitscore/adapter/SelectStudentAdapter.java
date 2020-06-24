package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.submitscore.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.submitscore.SubmitScoreActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectStudentAdapter extends RecyclerView.Adapter<SelectStudentAdapter.SelectStudentViewHolder> {

    private ArrayList<String> studentArrayList;

    public SelectStudentAdapter(ArrayList<String> studentArrayList) {
        this.studentArrayList = studentArrayList;
    }

    @NonNull
    @Override
    public SelectStudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_student, parent, false);

        return new SelectStudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectStudentViewHolder holder, int position) {
        if (studentArrayList.get(position)!=null){
            FirebaseDatabase.getInstance().getReference("Users").child(studentArrayList.get(position)).child("name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);
                    if (name!=null){
                        holder.studentName.setText(name);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    holder.itemView.getContext().getString(R.string.retrieve_failed);
                }
            });
        }

        holder.itemView.setOnClickListener((v) -> {
            Intent intent = new Intent(holder.itemView.getContext(), SubmitScoreActivity.class);
            intent.putExtra("studentUid", studentArrayList.get(position));
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (studentArrayList == null) ? 0 : studentArrayList.size();
    }


    public static class SelectStudentViewHolder extends RecyclerView.ViewHolder{

        private TextView studentName;

        public SelectStudentViewHolder(@NonNull View itemView) {
            super(itemView);

            studentName = itemView.findViewById(R.id.studentName);
        }
    }
}
