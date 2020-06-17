package com.example.learnbit.launch.extension;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.learnbit.R;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {

    //broadcast notification to device
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "REMINDER_1")
                .setSmallIcon(R.drawable.background_gradient)
                .setContentTitle("Class Reminder")
                .setContentText("Your class is going to start soon")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(200, builder.build());
    }
}
