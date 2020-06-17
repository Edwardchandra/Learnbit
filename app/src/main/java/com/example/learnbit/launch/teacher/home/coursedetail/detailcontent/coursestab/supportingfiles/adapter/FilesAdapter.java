package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.model.File;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FilesViewHolder> {

    private ArrayList<File> fileArray;

    public FilesAdapter(ArrayList<File> fileArray) {
        this.fileArray = fileArray;
    }

    @NonNull
    @Override
    public FilesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_teacher_supporting_files, parent, false);

        return new FilesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilesViewHolder holder, int position) {
        holder.fileName.setText(fileArray.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return (fileArray == null) ? 0 : fileArray.size();
    }

    public static class FilesViewHolder extends RecyclerView.ViewHolder{
        private TextView fileName;

        public FilesViewHolder(@NonNull View itemView) {
            super(itemView);

            fileName = itemView.findViewById(R.id.fileNameTV);
        }
    }
}
