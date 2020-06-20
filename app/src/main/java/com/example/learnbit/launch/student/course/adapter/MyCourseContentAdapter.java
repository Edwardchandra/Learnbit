package com.example.learnbit.launch.student.course.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnbit.R;
import com.example.learnbit.launch.student.course.material.MaterialActivity;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.model.Content;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.supportingfiles.SupportingFilesActivity;

import java.util.ArrayList;

public class MyCourseContentAdapter extends RecyclerView.Adapter<MyCourseContentAdapter.MyCourseContentViewHolder> {

    private ArrayList<Content> contentArrayList;
    private Context context;

    private String courseName;
    private static final String detailPreference = "DETAIL_PREFERENCE";

    private String courseWeek;

    public MyCourseContentAdapter(ArrayList<Content> contentArrayList, Context context, String courseWeek) {
        this.contentArrayList = contentArrayList;
        this.context = context;
        this.courseWeek = courseWeek;

        SharedPreferences preferences = context.getSharedPreferences(detailPreference, Context.MODE_PRIVATE);
        courseName = preferences.getString("courseName", "");
    }

    @NonNull
    @Override
    public MyCourseContentAdapter.MyCourseContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_teacher_course_contents, parent, false);

        return new MyCourseContentAdapter.MyCourseContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCourseContentAdapter.MyCourseContentViewHolder holder, int position) {
        holder.contentName.setText(contentArrayList.get(position).getSectionTopicName());
        holder.contentType.setText(contentArrayList.get(position).getSectionTopicType());

        holder.contentMaterialButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, MaterialActivity.class);
            intent.putExtra("courseName", courseName);
            intent.putExtra("courseSectionTopic", contentArrayList.get(position).getSectionTopicName());
            intent.putExtra("courseSectionPart", contentArrayList.get(position).getSectionPart());
            intent.putExtra("courseSectionType", contentArrayList.get(position).getSectionTopicType());
            intent.putExtra("courseWeek", courseWeek);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (contentArrayList == null) ? 0 : contentArrayList.size();
    }

    static class MyCourseContentViewHolder extends RecyclerView.ViewHolder {

        private TextView contentName, contentType;
        private Button contentMaterialButton;

        MyCourseContentViewHolder(@NonNull View itemView) {
            super(itemView);

            contentName = itemView.findViewById(R.id.contentTitle);
            contentType = itemView.findViewById(R.id.contentType);
            contentMaterialButton = itemView.findViewById(R.id.courseMaterialButton);
        }
    }
}
