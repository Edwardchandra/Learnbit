package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.adapter;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.model.Material;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder> {

    private ArrayList<Material> materialArrayList;
    private String courseWeek;
    private String courseName;
    private String courseSectionPart;
    private String courseSectionTopic;

    public MaterialAdapter(ArrayList<Material> materialArrayList, String courseWeek, String courseName, String courseSectionPart, String courseSectionTopic) {
        this.materialArrayList = materialArrayList;
        this.courseName = courseName;
        this.courseWeek = courseWeek;
        this.courseSectionPart = courseSectionPart;
        this.courseSectionTopic = courseSectionTopic;
    }

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_teacher_supporting_files, parent, false);

        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        holder.materialName.setText(materialArrayList.get(position).getMaterialName());

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        holder.itemView.setOnClickListener((v) -> {
            if (firebaseUser!=null){
                FirebaseStorage.getInstance().getReference("Course").child(firebaseUser.getUid())
                        .child(courseName)
                        .child(courseWeek)
                        .child("topics")
                        .child(courseSectionPart)
                        .child(courseSectionTopic)
                        .child(materialArrayList.get(position).getMaterialName())
                        .getDownloadUrl().addOnSuccessListener(uri -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/pdf");

                    Intent chooser = Intent.createChooser(intent, "Open");
                    chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    holder.itemView.getContext().startActivity(chooser);
                }).addOnFailureListener(e -> {
                    Log.d("Failure", "failed to retrieve url");
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return (materialArrayList == null) ? 0 : materialArrayList.size();
    }

    class MaterialViewHolder extends RecyclerView.ViewHolder{

        private TextView materialName;

        MaterialViewHolder(@NonNull View itemView) {
            super(itemView);

            materialName = itemView.findViewById(R.id.fileNameTV);
            ProgressBar materialProgressBar = itemView.findViewById(R.id.fileProgressBar);

            materialProgressBar.setVisibility(View.GONE);
        }
    }
}
