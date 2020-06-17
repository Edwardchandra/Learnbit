package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.model.Submit;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class SubmitAdapter extends RecyclerView.Adapter<SubmitAdapter.SubmitViewHolder> {

    //variables
    private ArrayList<Submit> submitArrayList;
    private String courseWeek;
    private String courseName;
    private String courseSectionPart;

    public SubmitAdapter(ArrayList<Submit> submitArrayList, String courseWeek, String courseName, String courseSectionPart) {
        this.submitArrayList = submitArrayList;
        this.courseWeek = courseWeek;
        this.courseName = courseName;
        this.courseSectionPart = courseSectionPart;
    }

    @NonNull
    @Override
    public SubmitAdapter.SubmitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_submitted_files, parent, false);

        return new SubmitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubmitAdapter.SubmitViewHolder holder, int position) {
        //set material name
        holder.submitName.setText(submitArrayList.get(position).getSubmitFileName());

        //set uploader name
        FirebaseDatabase.getInstance().getReference("Users").child(submitArrayList.get(position).getUserUid()).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                if (name!=null){
                    holder.submitUser.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                holder.toast(holder.context.getString(R.string.retrieve_failed));
            }
        });

        //get current user
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //onclick cell method
        holder.itemView.setOnClickListener((v) -> {

            //check user is not null
            if (firebaseUser!=null){

                //retrieve data from storage
                FirebaseStorage.getInstance().getReference("Course").child(firebaseUser.getUid())
                        .child(courseName)
                        .child(courseWeek)
                        .child("topics")
                        .child(courseSectionPart)
                        .child("submitFile")
                        .child(submitArrayList.get(position).getSubmitFileName())
                        .getDownloadUrl().addOnSuccessListener(uri -> {

                    //display data in pdf viewer
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/pdf");
                    Intent chooser = Intent.createChooser(intent, "Open");
                    chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    holder.context.startActivity(chooser);
                }).addOnFailureListener(e -> holder.toast(holder.context.getString(R.string.retrieve_failed)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return (submitArrayList == null) ? 0 : submitArrayList.size();
    }

    public static class SubmitViewHolder extends RecyclerView.ViewHolder{

        //elements variables
        private TextView submitName, submitUser;

        //variables
        private Context context;

        public SubmitViewHolder(@NonNull View itemView) {
            super(itemView);
            submitName = itemView.findViewById(R.id.fileNameTV);
            submitUser = itemView.findViewById(R.id.uploaderNameTV);
            context = itemView.getContext();
        }

        //show toast
        private void toast(String message){
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
