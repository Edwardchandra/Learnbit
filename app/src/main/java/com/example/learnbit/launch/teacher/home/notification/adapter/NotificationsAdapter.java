package com.example.learnbit.launch.teacher.home.notification.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.model.userdata.Notifications;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    private ArrayList<Notifications> notificationsArrayList;

    public NotificationsAdapter(ArrayList<Notifications> notificationsArrayList) {
        this.notificationsArrayList = notificationsArrayList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_notification, parent, false);

        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.title.setText(notificationsArrayList.get(position).getTitle());
        holder.dateTime.setText(notificationsArrayList.get(position).getDateTime());
        holder.body.setText(notificationsArrayList.get(position).getBody());
    }

    @Override
    public int getItemCount() {
        return (notificationsArrayList == null) ? 0 : notificationsArrayList.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView title, dateTime, body;
        private Context context;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.notificationTitle);
            dateTime = itemView.findViewById(R.id.notificationDateTime);
            body = itemView.findViewById(R.id.notificationBody);

            context = itemView.getContext();

            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(context, "bisa", Toast.LENGTH_SHORT).show();
        }
    }
}
