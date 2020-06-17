package com.example.learnbit.launch.teacher.home.notification.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learnbit.R;
import com.example.learnbit.launch.model.userdata.Notifications;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    //initiate variable
    private ArrayList<Notifications> notificationsArrayList;

    //constructor
    public NotificationsAdapter(ArrayList<Notifications> notificationsArrayList) {
        this.notificationsArrayList = notificationsArrayList;
    }

    //inflate view with layout
    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_notification, parent, false);

        return new NotificationViewHolder(view);
    }

    //populate recyclerview cell elements with data
    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.title.setText(notificationsArrayList.get(position).getTitle());
        holder.dateTime.setText(notificationsArrayList.get(position).getDateTime());
        holder.body.setText(notificationsArrayList.get(position).getBody());
    }

    //set recyclerview cell size
    @Override
    public int getItemCount() {
        return (notificationsArrayList == null) ? 0 : notificationsArrayList.size();
    }

    //view holder class to initiate and customize elements from layout file
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        //initiate elements variables
        private TextView title, dateTime, body;

        //constructor
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.notificationTitle);
            dateTime = itemView.findViewById(R.id.notificationDateTime);
            body = itemView.findViewById(R.id.notificationBody);
        }
    }
}
