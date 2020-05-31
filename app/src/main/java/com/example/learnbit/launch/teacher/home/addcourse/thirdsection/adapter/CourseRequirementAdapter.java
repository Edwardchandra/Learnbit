package com.example.learnbit.launch.teacher.home.addcourse.thirdsection.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.addcourse.thirdsection.model.Requirement;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CourseRequirementAdapter extends RecyclerView.Adapter<CourseRequirementAdapter.CourseRequirementViewHolder> {

    ArrayList<Requirement> requirementArrayList = new ArrayList<>();
    Context context;

    public CourseRequirementAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CourseRequirementAdapter.CourseRequirementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_requirements_edittext, parent, false);

        return new CourseRequirementAdapter.CourseRequirementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseRequirementAdapter.CourseRequirementViewHolder holder, final int position) {
        holder.courseRequirementET.setHint("Enter your course requirement");
        holder.courseRequirementET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                requirementArrayList.get(position).setRequirement(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public int getItemCount() {
        return (requirementArrayList == null) ? 0 : requirementArrayList.size();
    }

    public class CourseRequirementViewHolder extends RecyclerView.ViewHolder{

        private EditText courseRequirementET;

        public CourseRequirementViewHolder(@NonNull View itemView) {
            super(itemView);

            courseRequirementET = (EditText) itemView.findViewById(R.id.addCourse_CourseRequirements);
        }
    }

    public void addEditText(Requirement requirement){
        requirementArrayList.add(requirement);
        notifyItemInserted(requirementArrayList.size() - 1);
    }

    public ArrayList<Requirement> getArrayList(){
        return requirementArrayList;
    }
}
