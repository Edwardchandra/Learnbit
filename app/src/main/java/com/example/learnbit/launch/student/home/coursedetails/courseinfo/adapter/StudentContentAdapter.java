package com.example.learnbit.launch.student.home.coursedetails.courseinfo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.model.Content;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StudentContentAdapter extends RecyclerView.Adapter<StudentContentAdapter.ContentViewHolder> {

    private ArrayList<Content> contentArrayList;
    private Context context;

    StudentContentAdapter(ArrayList<Content> contentArrayList, Context context) {
        this.contentArrayList = contentArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_curriculum_content, parent, false);

        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        holder.contentName.setText(contentArrayList.get(position).getSectionTopicName());
        holder.contentType.setText(contentArrayList.get(position).getSectionTopicType());
    }

    @Override
    public int getItemCount() {
        return (contentArrayList == null) ? 0 : contentArrayList.size();
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {

        private TextView contentName, contentType;

        ContentViewHolder(@NonNull View itemView) {
            super(itemView);

            contentName = itemView.findViewById(R.id.contentTitle);
            contentType = itemView.findViewById(R.id.contentType);
        }
    }
}
