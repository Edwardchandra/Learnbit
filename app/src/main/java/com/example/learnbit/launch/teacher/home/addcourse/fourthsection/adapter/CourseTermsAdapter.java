package com.example.learnbit.launch.teacher.home.addcourse.fourthsection.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.addcourse.fourthsection.model.Terms;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CourseTermsAdapter extends RecyclerView.Adapter<CourseTermsAdapter.CourseTermsViewHolder> {

    ArrayList<Terms> termsArrayList = new ArrayList<>();

    public CourseTermsAdapter(ArrayList<Terms> termsArrayList) {
        this.termsArrayList = termsArrayList;
    }

    @NonNull
    @Override
    public CourseTermsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.terms_view, parent, false);

        return new CourseTermsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseTermsViewHolder holder, int position) {
        holder.termsTV.setText(termsArrayList.get(position).getTerms());
    }

    @Override
    public int getItemCount() {
        return termsArrayList.size();
    }

    public class CourseTermsViewHolder extends RecyclerView.ViewHolder{

        private TextView termsTV;

        public CourseTermsViewHolder(@NonNull View itemView) {
            super(itemView);

            termsTV = (TextView) itemView.findViewById(R.id.addCourse_TermsConditions);
        }
    }
}
