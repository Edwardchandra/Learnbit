package com.example.learnbit.launch.reusableactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learnbit.R;
import com.example.learnbit.launch.extension.AudioPlayer;
import com.example.learnbit.launch.extension.BaseActivity;
import com.example.learnbit.launch.extension.SinchService;
import com.example.learnbit.launch.teacher.home.addcourse.secondsection.model.Time;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallState;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CallScreenActivity extends BaseActivity implements View.OnClickListener {

    static final String TAG = CallScreenActivity.class.getSimpleName();
    static final String CALL_START_TIME = "callStartTime";
    static final String ADDED_LISTENER = "addedListener";

    private AudioPlayer audioPlayer;
    private Timer timer;
    private UpdateCallDurationTask durationTask;

    private String callId;
    private long callStart = 0;
    private boolean addedListener = false;
    private boolean videoViewsAdded = false;

    private TextView callDuration, callState, callerName;

    private Button toggleCameraButton, toggleMicButton, toggleVideoButton, toggleSpeakerButton;

    private Boolean micDisabled, videoDisabled, speakerDisabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_screen);

        audioPlayer = new AudioPlayer(this);

        callDuration = findViewById(R.id.call_time);
        callState = findViewById(R.id.call_state);
        callerName = findViewById(R.id.caller_name);

        toggleMicButton = findViewById(R.id.toggle_mic_button);
        toggleVideoButton = findViewById(R.id.toggle_video_button);
        toggleCameraButton = findViewById(R.id.toggle_camera_button);
        toggleSpeakerButton = findViewById(R.id.toggle_speaker_button);
        Button endCallButton = findViewById(R.id.call_end_button);

        endCallButton.setOnClickListener(this);
        toggleMicButton.setOnClickListener(this);
        toggleVideoButton.setOnClickListener(this);
        toggleSpeakerButton.setOnClickListener(this);

        videoDisabled = false;
        micDisabled = false;
        speakerDisabled = false;

        if (savedInstanceState == null){
            callStart = System.currentTimeMillis();
        }

        retrieveIntentData();
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        Call call = getSinchServiceInterface().getCall(callId);
        if (call!=null){
            if (!addedListener){
                call.addCallListener(new SinchCallListener());
                addedListener = true;
            }
        }else{
            Log.e(TAG, "call with invalid call id, abort");
            finish();
        }

        updateUI();
    }

    private void updateUI(){
        if (getSinchServiceInterface()==null){
            return;
        }

        Call call = getSinchServiceInterface().getCall(callId);
        if (call!=null){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(call.getRemoteUserId()).child("name");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
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

            callState.setText(call.getState().toString());

            if (call.getState() == CallState.ESTABLISHED){
                addVideoView();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        durationTask.cancel();
        timer.cancel();
        removeVideoView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        timer = new Timer();
        durationTask = new UpdateCallDurationTask();
        timer.schedule(durationTask, 0 , 500);
        updateUI();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void endCall(){
        audioPlayer.stopProgressTone();
        Call call = getSinchServiceInterface().getCall(callId);
        if (call!=null){
            call.hangup();
        }
        finish();
    }

    private String formatTimeSpan(long timeSpan){
        long totalSeconds = timeSpan / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private void updateCallDuration(){
        if (callStart>0){
            callDuration.setText(formatTimeSpan(System.currentTimeMillis() - callStart));
        }
    }

    private void addVideoView(){
        if (videoViewsAdded || getSinchServiceInterface() == null){
            return;
        }

        final VideoController videoController = getSinchServiceInterface().getVideoController();
        if (videoController!=null){
            LinearLayout localView = findViewById(R.id.local_video_view);
            LinearLayout remoteView = findViewById(R.id.remote_video_view);

            localView.addView(videoController.getLocalView());
            remoteView.addView(videoController.getRemoteView());

            toggleCameraButton.setOnClickListener((v) -> videoController.toggleCaptureDevicePosition());

            videoViewsAdded = true;
        }
    }

    private void removeVideoView(){
        if (getSinchServiceInterface()==null){
            return;
        }

        VideoController videoController = getSinchServiceInterface().getVideoController();
        if (videoController!=null){
            LinearLayout localView = findViewById(R.id.local_video_view);
            LinearLayout remoteView = findViewById(R.id.remote_video_view);

            localView.removeView(videoController.getLocalView());
            remoteView.removeView(videoController.getRemoteView());

            videoViewsAdded = false;
        }
    }

    private void retrieveIntentData(){
        callId = getIntent().getStringExtra(SinchService.CALL_ID);
    }

    private void pauseVideo(){
        Call call = getSinchServiceInterface().getCall(callId);
        if (call!=null){
            call.pauseVideo();
            toggleVideoButton.setBackground(getResources().getDrawable(R.drawable.icon_call_video_off));
        }
    }

    private void resumeVideo(){
        Call call = getSinchServiceInterface().getCall(callId);
        if (call!=null){
            call.resumeVideo();
            toggleVideoButton.setBackground(getResources().getDrawable(R.drawable.icon_call_video_on));
        }
    }

    private void muteVoice(){
        Call call = getSinchServiceInterface().getCall(callId);
        if (call!=null){
            getSinchServiceInterface().getAudioController().mute();
            toggleMicButton.setBackground(getResources().getDrawable(R.drawable.icon_call_mic_off));
        }
    }

    private void unmuteVoice(){
        Call call = getSinchServiceInterface().getCall(callId);
        if (call!=null){
            getSinchServiceInterface().getAudioController().unmute();
            toggleMicButton.setBackground(getResources().getDrawable(R.drawable.icon_call_mic_on));
        }
    }

    private void switchAudioToSpeaker(){
        Call call = getSinchServiceInterface().getCall(callId);
        if (call!=null){
            getSinchServiceInterface().getAudioController().enableSpeaker();
            toggleSpeakerButton.setBackground(getResources().getDrawable(R.drawable.icon_call_speaker_on));
        }
    }

    private void switchAudioToNormal(){
        Call call = getSinchServiceInterface().getCall(callId);
        if (call!=null){
            getSinchServiceInterface().getAudioController().disableSpeaker();
            toggleSpeakerButton.setBackground(getResources().getDrawable(R.drawable.icon_call_speaker_off));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.call_end_button:
                endCall();
                break;
            case R.id.toggle_speaker_button:
                if (speakerDisabled){
                    switchAudioToSpeaker();
                    speakerDisabled = false;
                }else{
                    switchAudioToNormal();
                }
                break;
            case R.id.toggle_video_button:
                if (!videoDisabled){
                    pauseVideo();
                    videoDisabled = true;
                }else{
                    resumeVideo();
                }
                break;
            case R.id.toggle_mic_button:
                if (!micDisabled){
                    muteVoice();
                    micDisabled = true;
                }else{
                    unmuteVoice();
                }
                break;
        }
    }

    private class SinchCallListener implements VideoCallListener{

        @Override
        public void onVideoTrackAdded(Call call) {
            if (videoViewsAdded || getSinchServiceInterface() == null){
                return;
            }

            final VideoController videoController = getSinchServiceInterface().getVideoController();
            if (videoController!=null){
                LinearLayout localView = findViewById(R.id.local_video_view);
                LinearLayout remoteView = findViewById(R.id.remote_video_view);

                localView.addView(videoController.getLocalView());
                remoteView.addView(videoController.getRemoteView());

                toggleCameraButton.setOnClickListener((v) -> videoController.toggleCaptureDevicePosition());

                videoViewsAdded = true;
            }
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
            audioPlayer.playProgressTone();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
            audioPlayer.stopProgressTone();
            callState.setText(call.getState().toString());
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            AudioController audioController = getSinchServiceInterface().getAudioController();
            audioController.enableSpeaker();
            callStart = System.currentTimeMillis();
            Log.d(TAG, "Call offered video: " + call.getDetails().isVideoOffered());
        }

        @Override
        public void onCallEnded(Call call) {
            CallEndCause callEndCause = call.getDetails().getEndCause();
            Toast.makeText(CallScreenActivity.this, callEndCause.toString() + " ", Toast.LENGTH_SHORT).show();
            audioPlayer.stopProgressTone();

            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            String endMsg = "Call ended: " + call.getDetails().toString();
            Toast.makeText(CallScreenActivity.this, endMsg, Toast.LENGTH_LONG).show();

            endCall();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }

    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            CallScreenActivity.this.runOnUiThread(CallScreenActivity.this::updateCallDuration);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(CALL_START_TIME, callStart);
        outState.putBoolean(ADDED_LISTENER, addedListener);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        callStart = savedInstanceState.getLong(CALL_START_TIME);
        addedListener = savedInstanceState.getBoolean(ADDED_LISTENER);
    }
}