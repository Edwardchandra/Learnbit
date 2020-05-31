package com.example.learnbit.launch.student.home.coursedetails.courseinfo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.learnbit.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BenefitAdapter extends RecyclerView.Adapter<BenefitAdapter.BenefitViewHolder> {

    private ArrayList<String> benefitArrayList;

    public BenefitAdapter(ArrayList<String> benefitArrayList) {
        this.benefitArrayList = benefitArrayList;
    }

    @NonNull
    @Override
    public BenefitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_benefits_point, parent, false);

        return new BenefitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BenefitViewHolder holder, int position) {
        holder.benefit.setText(benefitArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return (benefitArrayList == null) ? 0 : benefitArrayList.size();
    }

    static class BenefitViewHolder extends RecyclerView.ViewHolder{

        private TextView benefit;

        BenefitViewHolder(@NonNull View itemView) {
            super(itemView);

            benefit = itemView.findViewById(R.id.courseBenefit);
        }
    }
}
