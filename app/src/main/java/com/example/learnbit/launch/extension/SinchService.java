package com.example.learnbit.launch.extension;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.learnbit.launch.reusableactivity.IncomingCallActivity;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.video.VideoController;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;

public class SinchService extends Service {

    //sinch service key and environment, used for configuration, retrieved from sinch console
    private static final String APP_KEY = "17dac3f9-0e67-40ba-97ca-c5f0e6418232";
    private static final String APP_SECRET = "SUgKIQiZd06VGV3GweurqA==";
    private static final String ENVIRONMENT = "sandbox.sinch.com";

    //set call id
    public static final String CALL_ID = "CALL_ID";

    //initiate sinch service interface, client, and user id
    private SinchServiceInterface sinchServiceInterface = new SinchServiceInterface();
    private com.sinch.android.rtc.SinchClient sinchClient;

    //initiate sinch startFailed listener
    private StartFailedListener startFailedListener;

    //if service is created
    @Override
    public void onCreate() {
        super.onCreate();
    }

    //if service is destroyed
    //check sinch service is null/not and is started/not
    //if sinch service is not null and started, then terminate sinch service
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (sinchClient!=null && sinchClient.isStarted()){
            sinchClient.terminate();
        }
    }

    //start sinch client using user id
    private void start(String userID){

        //check if sinch client is null/not
        if (sinchClient == null){

            //set sinch client
            sinchClient = Sinch.getSinchClientBuilder().context(getApplicationContext()).userId(userID)
                    .applicationKey(APP_KEY)
                    .applicationSecret(APP_SECRET)
                    .environmentHost(ENVIRONMENT).build();

            //set call support for sinch client
            //sinch client will start listening for incoming call
            sinchClient.setSupportCalling(true);
            sinchClient.startListeningOnActiveConnection();

            //set sinch client listener class
            //set call client listener class
            sinchClient.addSinchClientListener(new MySinchClientListener());
            sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());

            //start sinch client
            sinchClient.start();
        }
    }

    //stop sinch client service
    private void stop(){

        //check if sinch client is not null
        if (sinchClient!=null){

            //terminate sinch client and set sinch client value to null
            sinchClient.terminate();
            sinchClient = null;
        }
    }

    //if sinch client is started
    //return sinch client is not nul and sinch client is started
    private boolean isStarted(){
        return (sinchClient!=null && sinchClient.isStarted());
    }

    //set service bind
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sinchServiceInterface;
    }

    //sinch service interface class
    public class SinchServiceInterface extends Binder {

        //used when call button is clicked, establish connection with parameter userId
        public Call callUserVideo(String userID){
            return sinchClient.getCallClient().callUserVideo(userID);
        }

        //get sinch service start status
        public boolean isStarted(){
            return SinchService.this.isStarted();
        }

        //start sinch client with username
        public void startClient(String userName){
            start(userName);
        }

        //stop sinch client
        public void stopClient(){
            stop();
        }

        //set global startFailedListener to parameter startFailedListener
        public void setStartListener(StartFailedListener listener){
            startFailedListener = listener;
        }

        //get current call id
        public Call getCall(String callID){
            return sinchClient.getCallClient().getCall(callID);
        }

        //get video controller
        //video controller used to show video
        //controller can be used for various control(ie. pause stream video, etc)
        public VideoController getVideoController(){

            //terminate if is not started
            if (!isStarted()){
                return null;
            }

            //return sinch client video controller
            return sinchClient.getVideoController();
        }

        //get audio controller
        //used to output audio
        //controller can be used for various control(ie. mute, etc)
        public AudioController getAudioController(){

            //terminate if is not started
            if (!isStarted()){
                return null;
            }

            //return sinch client audio controller
            return sinchClient.getAudioController();
        }
    }

    //initiate interface, inherit interface method
    public interface StartFailedListener{
        void onStartFailed(SinchError error);
        void onStarted();
    }

    //sinch client listener class
    private class MySinchClientListener implements SinchClientListener{

        //if sinch client started, execute
        @Override
        public void onClientStarted(SinchClient sinchClient) {
            //check if startFailed listener is not null
            //if not null, start listener
            if (startFailedListener!=null){
                startFailedListener.onStarted();
            }
        }

        //if sinch client stopped, execute
        @Override
        public void onClientStopped(SinchClient sinchClient) {}

        //if sinch client failed to run, execute
        @Override
        public void onClientFailed(SinchClient client, SinchError sinchError) {
            //if listener is not null, return error
            if (startFailedListener!=null){
                startFailedListener.onStartFailed(sinchError);
            }

            //terminate sinch client and set null
            sinchClient.terminate();
            sinchClient = null;
        }

        //if registration credentials is not fulfilled
        @Override
        public void onRegistrationCredentialsRequired(SinchClient sinchClient, ClientRegistration clientRegistration) {}

        //log message for testing purpose
        @Override
        public void onLogMessage(int i, String s, String s1) {
            switch (i){
                case Log.DEBUG:
                    Log.d(s, s1);
                    break;
                case Log.ERROR:
                    Log.e(s, s1);
                    break;
                case Log.INFO:
                    Log.i(s, s1);
                    break;
                case Log.VERBOSE:
                    Log.v(s, s1);
                    break;
                case Log.WARN:
                    Log.w(s, s1);
                    break;
            }
        }
    }

    //sinch call client listener class
    private class SinchCallClientListener implements CallClientListener{

        //if there's incoming call, execute
        @Override
        public void onIncomingCall(CallClient callClient, Call call) {

            //start incoming call activity
            Intent intent = new Intent(SinchService.this, IncomingCallActivity.class);
            intent.putExtra(CALL_ID, call.getCallId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            SinchService.this.startActivity(intent);
        }
    }
}
