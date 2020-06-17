package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.model.Material;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder> {

    //variables
    private ArrayList<Material> materialArrayList;
    private String courseWeek;
    private String courseName;
    private String courseSectionPart;
    private String courseSectionTopic;

    //constructor
    public MaterialAdapter(ArrayList<Material> materialArrayList, String courseWeek, String courseName, String courseSectionPart, String courseSectionTopic) {
        this.materialArrayList = materialArrayList;
        this.courseName = courseName;
        this.courseWeek = courseWeek;
        this.courseSectionPart = courseSectionPart;
        this.courseSectionTopic = courseSectionTopic;
    }

    //inflate with layout file
    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_teacher_supporting_files, parent, false);

        return new MaterialViewHolder(view);
    }

    //set recyclerview item data
    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {

        //set material name
        holder.materialName.setText(materialArrayList.get(position).getMaterialName());

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
                        .child(courseSectionTopic)
                        .child(materialArrayList.get(position).getMaterialName())
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

    //set recyclerview cell size
    @Override
    public int getItemCount() {
        return (materialArrayList == null) ? 0 : materialArrayList.size();
    }

    //recyclerview viewholder
    static class MaterialViewHolder extends RecyclerView.ViewHolder{

        //elements variables
        private TextView materialName;

        //variables
        private Context context;

        //constructor
        MaterialViewHolder(@NonNull View itemView) {
            super(itemView);
            materialName = itemView.findViewById(R.id.fileNameTV);
            context = itemView.getContext();
        }

        //show toast
        private void toast(String message){
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
