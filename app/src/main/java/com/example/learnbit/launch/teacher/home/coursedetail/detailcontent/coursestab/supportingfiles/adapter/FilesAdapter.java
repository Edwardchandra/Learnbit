package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.model.File;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FilesViewHolder> {

    private ArrayList<File> fileArray;
    private Context context;

    public FilesAdapter(ArrayList<File> fileArray, Context context) {
        this.fileArray = fileArray;
        this.context = context;
    }

    @NonNull
    @Override
    public FilesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.supporting_files_view, parent, false);

        return new FilesViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull FilesViewHolder holder, int position) {
        holder.fileName.setText(fileArray.get(position).getName());

        if (fileArray.get(position).getUpload()){
            holder.fileProgressBar.setVisibility(View.INVISIBLE);
            holder.doneImageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return (fileArray == null) ? 0 : fileArray.size();
    }

    public class FilesViewHolder extends RecyclerView.ViewHolder{

        private TextView fileName;
        private ProgressBar fileProgressBar;
        private ImageView doneImageView;

        public FilesViewHolder(@NonNull View itemView) {
            super(itemView);

            fileName = itemView.findViewById(R.id.fileNameTV);
            fileProgressBar = itemView.findViewById(R.id.fileProgressBar);
            doneImageView = itemView.findViewById(R.id.doneImageView);
        }
    }
}
