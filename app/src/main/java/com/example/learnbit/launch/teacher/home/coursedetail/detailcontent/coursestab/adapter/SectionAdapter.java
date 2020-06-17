package com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.model.Content;
import com.example.learnbit.launch.teacher.home.coursedetail.detailcontent.coursestab.model.Section;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionViewHolder> {

    //variable
    private ArrayList<Section> sectionArrayList;

    //construcotr
    public SectionAdapter(ArrayList<Section> sectionArrayList) {
        this.sectionArrayList = sectionArrayList;
    }

    //set layout file
    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_teacher_course_sections, parent, false);

        return new SectionViewHolder(view);
    }

    //set data for cell
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        holder.sectionName.setText(holder.context.getString(R.string.divider, sectionArrayList.get(position).getWeek(), sectionArrayList.get(position).getName()));

        for (HashMap.Entry<String, Content> entry : sectionArrayList.get(position).getTopics().entrySet()){
            String key = entry.getKey();
            Content value = entry.getValue();

            holder.contentArrayList.add(new Content(key, value.getSectionTopicName(), value.getSectionTopicType()));
            holder.contentArrayList.sort(Comparator.comparing(Content::getSectionPart));
        }

        holder.setupRecyclerView(sectionArrayList.get(position).getWeek());
    }

    //set cell count
    @Override
    public int getItemCount() {
        return (sectionArrayList == null) ? 0 : sectionArrayList.size();
    }

    //view holder
    public static class SectionViewHolder extends RecyclerView.ViewHolder{

        //element variable
        private TextView sectionName;
        private RecyclerView contentRV;
        private Context context;
        private ArrayList<Content> contentArrayList = new ArrayList<>();

        //constructor
        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);

            sectionName = itemView.findViewById(R.id.teacherCourse_SectionName);
            contentRV = itemView.findViewById(R.id.teacherCourse_ContentRecyclerView);
            context = itemView.getContext();
        }

        //setup content recyclerview
        private void setupRecyclerView(String week){
            ContentAdapter contentAdapter = new ContentAdapter(contentArrayList, context, week);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            contentRV.setLayoutManager(layoutManager);
            contentRV.setAdapter(contentAdapter);
        }
    }
}
