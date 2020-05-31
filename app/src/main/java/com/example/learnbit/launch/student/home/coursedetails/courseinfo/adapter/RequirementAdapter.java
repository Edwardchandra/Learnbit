package com.example.learnbit.launch.student.home.coursedetails.courseinfo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.learnbit.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RequirementAdapter extends RecyclerView.Adapter<RequirementAdapter.RequirementViewHolder> {

    private ArrayList<String> requirementArrayList;

    public RequirementAdapter(ArrayList<String> requirementArrayList) {
        this.requirementArrayList = requirementArrayList;
    }

    @NonNull
    @Override
    public RequirementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_requirement_point, parent, false);

        return new RequirementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequirementViewHolder holder, int position) {
        holder.requirement.setText(requirementArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return (requirementArrayList == null) ? 0 : requirementArrayList.size();
    }

    static class RequirementViewHolder extends RecyclerView.ViewHolder{

        private TextView requirement;

        RequirementViewHolder(@NonNull View itemView) {
            super(itemView);

            requirement = itemView.findViewById(R.id.courseRequirement);
        }
    }
}
