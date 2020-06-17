package com.example.learnbit.launch.teacher.profile.withdraw.history.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.profile.withdraw.history.details.WithdrawDetailsActivity;
import com.example.learnbit.launch.teacher.profile.withdraw.model.Withdraw;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WithdrawHistoryAdapter extends RecyclerView.Adapter<WithdrawHistoryAdapter.WithdrawHistoryViewHolder> {

    private ArrayList<Withdraw> withdrawArrayList;
    private ArrayList<String> key;

    public WithdrawHistoryAdapter(ArrayList<Withdraw> withdrawArrayList, ArrayList<String> key) {
        this.withdrawArrayList = withdrawArrayList;
        this.key = key;
    }

    @NonNull
    @Override
    public WithdrawHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_withdraw, parent, false);

        return new WithdrawHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WithdrawHistoryViewHolder holder, int position) {
        holder.title.setText(holder.itemView.getContext().getString(R.string.withdraw_title, withdrawArrayList.get(position).getAmount()));
        holder.status.setText(withdrawArrayList.get(position).getSent());
        holder.dateTime.setText(withdrawArrayList.get(position).getDateTime());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), WithdrawDetailsActivity.class);
            intent.putExtra("key", key.get(position));
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (withdrawArrayList == null) ? 0 : withdrawArrayList.size();
    }

    public static class WithdrawHistoryViewHolder extends RecyclerView.ViewHolder{
        private TextView title, status, dateTime;

        public WithdrawHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.withdrawTitle);
            status = itemView.findViewById(R.id.withdrawStatus);
            dateTime = itemView.findViewById(R.id.withdrawDateTime);
        }
    }
}
