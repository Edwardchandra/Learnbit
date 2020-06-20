package com.example.learnbit.launch.student.home.category.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnbit.R;
import com.example.learnbit.launch.student.home.category.subcategory.SubcategoryActivity;
import com.example.learnbit.launch.student.home.model.Category;

import java.util.ArrayList;

public class SubcategoryAdapter extends RecyclerView.Adapter<SubcategoryAdapter.SubcategoryViewHolder> {

    private ArrayList<Category> subcategoryArrayList;

    public SubcategoryAdapter(ArrayList<Category> subcategoryArrayList) {
        this.subcategoryArrayList = subcategoryArrayList;
    }

    @NonNull
    @Override
    public SubcategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_category, parent, false);

        return new SubcategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubcategoryViewHolder holder, int position) {
        holder.subcategoryName.setText(subcategoryArrayList.get(position).getName());
        holder.subcategoryImage.setImageResource(subcategoryArrayList.get(position).getImage());

        holder.itemView.setOnClickListener((v) -> {
            Intent intent = new Intent(holder.itemView.getContext(), SubcategoryActivity.class);
            intent.putExtra("subcategoryName", subcategoryArrayList.get(position).getName());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (subcategoryArrayList == null) ? 0 : subcategoryArrayList.size();
    }

    public static class SubcategoryViewHolder extends RecyclerView.ViewHolder{

        private TextView subcategoryName;
        private ImageView subcategoryImage;

        public SubcategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            subcategoryName = itemView.findViewById(R.id.categoryName);
            subcategoryImage = itemView.findViewById(R.id.categoryImageView);
        }
    }
}
