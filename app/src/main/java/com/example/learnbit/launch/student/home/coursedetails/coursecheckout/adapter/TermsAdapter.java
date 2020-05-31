package com.example.learnbit.launch.student.home.coursedetails.coursecheckout.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.learnbit.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TermsAdapter extends RecyclerView.Adapter<TermsAdapter.TermsViewHolder> {

    private ArrayList<String> termsArrayList;

    public TermsAdapter(ArrayList<String> termsArrayList) {
        this.termsArrayList = termsArrayList;
    }

    @NonNull
    @Override
    public TermsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_terms, parent, false);

        return new TermsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TermsViewHolder holder, int position) {
        holder.terms.setText(termsArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return (termsArrayList == null) ? 0 : termsArrayList.size();
    }

    static class TermsViewHolder extends RecyclerView.ViewHolder{

        private TextView terms;

        public TermsViewHolder(@NonNull View itemView) {
            super(itemView);

            terms = itemView.findViewById(R.id.addCourse_TermsConditions);
        }
    }
}
