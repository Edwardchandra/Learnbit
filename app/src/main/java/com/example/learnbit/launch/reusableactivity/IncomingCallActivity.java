package com.example.learnbit.launch.reusableactivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;
import com.example.learnbit.R;
import com.example.learnbit.launch.extension.AudioPlayer;
import com.example.learnbit.launch.extension.BaseActivity;
import com.example.learnbit.launch.extension.SinchService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.video.VideoCallListener;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class IncomingCallActivity extends BaseActivity implements View.OnClickListener {

    private ImageView backgroundView;
    private TextView callerName;
    private ImageView callerImageView;

    private Button acceptCallButton, declineCallButton;

    static final String TAG = IncomingCallActivity.class.getSimpleName();
    private String callId;
    private AudioPlayer audioPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        backgroundView = findViewById(R.id.background_view);
        callerName = findViewById(R.id.caller_name);
        callerImageView = findViewById(R.id.caller_image_view);
        acceptCallButton = findViewById(R.id.accept_call_button);
        declineCallButton  = findViewById(R.id.decline_call_button);

        acceptCallButton.setOnClickListener(this);
        declineCallButton.setOnClickListener(this);

        audioPlayer = new AudioPlayer(this);
        audioPlayer.playRingtone();

        retrieveIntentData();

        callerImageView.setClipToOutline(true);
    }

    private void retrieveIntentData(){
        callId = getIntent().getStringExtra(SinchService.CALL_ID);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        Call call = getSinchServiceInterface().getCall(callId);
        if (call!=null){
            call.addCallListener(new SinchCallListener());

            FirebaseDatabase.getInstance().getReference("Users").child(call.getRemoteUserId()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue(String.class);

                    if (name!=null) callerName.setText(name);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    callerName.setText(call.getRemoteUserId());
                }
            });

            FirebaseStorage.getInstance().getReference("Users").child(call.getRemoteUserId()).child("profileimage").getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Glide.with(getApplicationContext()).load(uri).apply(RequestOptions.bitmapTransform(new BlurTransformation(25,3))).into(backgroundView);
                        Glide.with(getApplicationContext()).load(uri).into(callerImageView);
                    }).addOnFailureListener(e -> Log.e("message", "failed to retrieve image"));
        }else{
            Log.e("message", "started with invalid call, abort");
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.accept_call_button:
                answerCall();
                break;
            case R.id.decline_call_button:
                declineCall();
                break;
        }
    }

    private void answerCall(){
        audioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(callId);
        if (call != null) {
            call.answer();
            Intent intent = new Intent(this, CallScreenActivity.class);
            intent.putExtra(SinchService.CALL_ID, callId);
            startActivity(intent);
        } else {
            finish();
        }
    }

    private void declineCall(){
        audioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(callId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private class SinchCallListener implements VideoCallListener {

        @Override
        public void onVideoTrackAdded(Call call) {

        }

        @Override
        public void onVideoTrackPaused(Call call) {

        }

        @Override
        public void onVideoTrackResumed(Call call) {

        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
        }

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended, cause: " + cause.toString());
            audioPlayer.stopRingtone();
            finish();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }
}