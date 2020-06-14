package com.example.learnbit.launch.extension;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.example.learnbit.R;
import com.example.learnbit.launch.teacher.TeacherMainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    //get current device token
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        sendRegistrationToServer(s);
    }

    //save device token value to firebase database
    private void sendRegistrationToServer(String token){

        //initiate firebase auth instance and get current user
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //initiate firebase database instance
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        //check if current user is null or not, if current user is not null then save current device token to firebase database
        if (user!=null){
            firebaseDatabase.getReference("Users").child(user.getUid()).child("token").setValue(token);
        }
    }

    //if message is received from firebase messaging service, execute this method
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //check if message is null or not
        //if message is not null then proceed
        if (remoteMessage.getNotification() != null) {

            //initiate variable to store message title and body
            String notificationTitle = remoteMessage.getNotification().getTitle();
            String notificationBody = remoteMessage.getNotification().getBody();

            //send push notification to device
            sendNotification(notificationTitle, notificationBody);
        }
    }

    //send push notification to device
    private void sendNotification(String messageTitle, String messageBody) {

        //create a new intent to send notification
        Intent intent = new Intent(this, TeacherMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        //set notification channel id
        String channelId = "learnbit01";

        //get current notification sound uri
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //build notification from provided data with current notification sound
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.background_gradient)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        //initiate notification manager
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //check if notification manager is null or not, if not null then proceed
        if (notificationManager!=null){

            //check current android os version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                //create notification channel
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(0, notificationBuilder.build());
        }
    }
}
