package com.example.learnbit.launch.student.home.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.learnbit.R;
import com.example.learnbit.launch.student.home.category.CategoryActivity;
import com.example.learnbit.launch.student.home.model.Category;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private ArrayList<Category> categoryArrayList;

    public CategoryAdapter(ArrayList<Category> categoryArrayList) {
        this.categoryArrayList = categoryArrayList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_category, parent, false);

        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.name.setText(categoryArrayList.get(position).getName());
        holder.image.setImageResource(categoryArrayList.get(position).getImage());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), CategoryActivity.class);
            intent.putExtra("categoryName", categoryArrayList.get(position).getName());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (categoryArrayList == null) ? 0 : categoryArrayList.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private ImageView image;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.categoryName);
            image = itemView.findViewById(R.id.categoryImageView);
        }
    }
}
