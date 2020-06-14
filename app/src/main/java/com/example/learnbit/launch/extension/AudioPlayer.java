package com.example.learnbit.launch.extension;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

import java.io.FileInputStream;
import java.io.IOException;

public class AudioPlayer {

    //initiate variables
    private Context context;
    private MediaPlayer mediaPlayer;
    private AudioTrack progressTone;
    private final static int SAMPLE_RATE = 16000;

    //set class context
    public AudioPlayer(Context context){
        this.context = context.getApplicationContext();
    }

    //play ringtone method when there's incoming call
    public void playRingtone(){

        //initiate audio manager
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        //retrieve device ringtone uri
        Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        //check if ringer mode is normal ringer mode
        //if true, proceed
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {

            //initiate media player and set audio stream type
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);

            //set media player data source from device ringtone uri which is initiated
            //catch error
            try {
                mediaPlayer.setDataSource(context, defaultRingtoneUri);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }

            //if media player is not null, start ringing and looping through the ringtone
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        }
    }

    //stop ringtone when call is accepted or declined
    public void stopRingtone(){

        //check if media player is null/not
        //if media player is not null then stop ringtone and release media player
        //if media player is null then terminate
        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    //start progress tone when start calling but user not yet answered
    public void playProgressTone(){
        stopProgressTone();
        try {
            progressTone = createProgressTone(context);
            if (progressTone!=null){
                progressTone.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //stop progress tone if call is started
    public void stopProgressTone() {
        if (progressTone != null) {
            progressTone.stop();
            progressTone.release();
            progressTone = null;
        }
    }

    //create progress tone, plays when call is initiated
    private static AudioTrack createProgressTone(Context context) throws IOException {

        //retrieve current ringtone uri
        Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        //initiate file descriptor,
        //file descriptor is used to open the data in a file(ie. length, etc)
        AssetFileDescriptor fd = context.getContentResolver().openAssetFileDescriptor(defaultRingtoneUri, "r");

        //check if file descriptor is null or not
        //if file descriptor is null then return null
        //if file descriptor is not null then return audiotrack
        if (fd!=null){

            //get the length of the data
            int length = (int) fd.getLength();

            //initiate audiotrack
            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, length, AudioTrack.MODE_STATIC);

            //initiate byte variable
            byte[] data = new byte[length];

            //convert file data to byte
            readFileToBytes(fd, data);

            //write the byte to audiotrack and set loop
            audioTrack.write(data, 0, data.length);
            audioTrack.setLoopPoints(0, data.length / 2, 30);

            return audioTrack;
        }

        return null;
    }

    //convert file to bytes
    private static void readFileToBytes(AssetFileDescriptor fd, byte[] data) throws IOException {
        FileInputStream inputStream = fd.createInputStream();

        int bytesRead = 0;
        while (bytesRead < data.length) {
            int res = inputStream.read(data, bytesRead, (data.length - bytesRead));
            if (res == -1) {
                break;
            }
            bytesRead += res;
        }
    }
}
