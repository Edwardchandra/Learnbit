package com.example.learnbit.launch.teacher.home.addcourse.thirdsection.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.home.addcourse.thirdsection.model.Benefit;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CourseBenefitAdapter extends RecyclerView.Adapter<CourseBenefitAdapter.CourseBenefitViewHolder> {

    ArrayList<Benefit> benefitArrayList = new ArrayList<>();
    Context context;

    public CourseBenefitAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CourseBenefitAdapter.CourseBenefitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_benefits_edittext, parent, false);

        return new CourseBenefitAdapter.CourseBenefitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseBenefitAdapter.CourseBenefitViewHolder holder, final int position) {
        holder.courseBenefitET.setHint("What will your student learn?");
        holder.courseBenefitET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                benefitArrayList.get(position).setBenefit(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public int getItemCount() {
        return (benefitArrayList == null) ? 0 : benefitArrayList.size();
    }

    public class CourseBenefitViewHolder extends RecyclerView.ViewHolder{

        private EditText courseBenefitET;

        public CourseBenefitViewHolder(@NonNull View itemView) {
            super(itemView);

            courseBenefitET = (EditText) itemView.findViewById(R.id.addCourse_CourseBenefits);
        }
    }

    public void addEditText(Benefit benefit){
        benefitArrayList.add(benefit);
        notifyItemInserted(benefitArrayList.size() - 1);
    }

    public ArrayList<Benefit> getArrayList(){
        return benefitArrayList;
    }
}
