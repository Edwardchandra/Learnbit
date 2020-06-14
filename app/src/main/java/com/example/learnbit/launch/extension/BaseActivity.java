package com.example.learnbit.launch.extension;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity implements ServiceConnection {

    private SinchService.SinchServiceInterface sinchServiceInterface;

    //onCreate method is executed when activity is created
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //bind sinch service class to activity
        getApplicationContext().bindService(new Intent(this, SinchService.class), this, BIND_AUTO_CREATE);
    }

    //if sinch service is connected then initiate sinch service interface
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        if (SinchService.class.getName().equals(name.getClassName())){
            sinchServiceInterface = (SinchService.SinchServiceInterface) service;
            onServiceConnected();
        }
    }

    //if sinch service is disconnected then remove sinch service interface
    @Override
    public void onServiceDisconnected(ComponentName name) {
        if (SinchService.class.getName().equals(name.getClassName())){
            sinchServiceInterface = null;
            onServiceDisconnected();
        }
    }

    //executed when sinch service is connected
    //it is used for subclass that extend this activity
    protected void onServiceConnected() {
        // for subclasses
    }

    //executed when sinch service is disconnected
    //it is used for subclass that extend this activity
    protected void onServiceDisconnected() {
        // for subclasses
    }

    //return sinch service interface for subclass that extend this activity
    public SinchService.SinchServiceInterface getSinchServiceInterface(){
        return sinchServiceInterface;
    }
}
