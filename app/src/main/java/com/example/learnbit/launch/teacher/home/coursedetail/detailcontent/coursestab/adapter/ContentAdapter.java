package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.model.Content;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.SupportingFilesActivity;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    private ArrayList<Content> contentArrayList = new ArrayList<>();
    private Context context;

    private String courseName;
    private static final String detailPreference = "DETAIL_PREFERENCE";

    private String courseWeek;

    public ContentAdapter(ArrayList<Content> contentArrayList, Context context, String courseWeek) {
        this.contentArrayList = contentArrayList;
        this.context = context;
        this.courseWeek = courseWeek;

        SharedPreferences preferences = context.getSharedPreferences(detailPreference, Context.MODE_PRIVATE);
        courseName = preferences.getString("courseName", "");
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.teacher_course_sections_contents, parent, false);

        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        holder.contentName.setText(contentArrayList.get(position).getSectionTopicName());
        holder.contentType.setText(contentArrayList.get(position).getSectionTopicType());

        holder.contentMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SupportingFilesActivity.class);
                intent.putExtra("courseName", courseName);
                intent.putExtra("courseSectionTopic", contentArrayList.get(position).getSectionTopicName());
                intent.putExtra("courseSectionPart", contentArrayList.get(position).getSectionPart());
                intent.putExtra("courseWeek", courseWeek);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (contentArrayList == null) ? 0 : contentArrayList.size();
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {

        private TextView contentName, contentType;
        private Button contentMaterialButton;

        ContentViewHolder(@NonNull View itemView) {
            super(itemView);

            contentName = itemView.findViewById(R.id.contentTitle);
            contentType = itemView.findViewById(R.id.contentType);
            contentMaterialButton = itemView.findViewById(R.id.courseMaterialButton);
        }
    }
}
