package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.model.Material;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder> {

    private ArrayList<Material> materialArrayList;

    public MaterialAdapter(ArrayList<Material> materialArrayList) {
        this.materialArrayList = materialArrayList;
    }

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.supporting_files_view, parent, false);

        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialViewHolder holder, int position) {
        holder.materialName.setText(materialArrayList.get(position).getMaterialName());
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
