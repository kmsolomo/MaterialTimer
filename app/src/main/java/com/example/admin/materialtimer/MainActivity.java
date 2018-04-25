package com.example.admin.materialtimer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity{

    public enum TimerState {
        Running, Stopped, Paused
    }

    private FloatingActionButton controlButton;
    private ImageButton settingsButton;
    private TextView timerView;

    private TimerUtility timer;
    private TimerState timerStatus = TimerState.Stopped;
    private long milliSecondsLeft;
    private final int THEME_REQUEST_CODE = 1;
    private NotificationUtil notificationUtil;
    private TimerService timerService;
    private boolean serviceBound = false;
    private Intent timerIntent;

    private ServiceConnection timerConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName className, IBinder service){
            TimerService.TimerBinder binder = (TimerService.TimerBinder) service;
            timerService = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0){
            serviceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtility.themeCheck(this);
        setContentView(R.layout.activity_main);

        //Bind Views
        controlButton = findViewById(R.id.floatingActionButton);
        timerView = findViewById(R.id.timerTextView);
        settingsButton = findViewById(R.id.SettingsButton);

        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);

        //insures service persists bound lifecycle
        timerIntent = new Intent(MainActivity.this, TimerService.class);
        startService(timerIntent);

        //Floating action button
        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timerStatus == TimerState.Running){

                    timerService.pauseTimer();
                    timerStatus = TimerState.Paused;
                    controlButton.setImageResource(R.drawable.ic_play_arrow_44dp);
                } else {

                    timerService.startTimer();
                    timerStatus = TimerState.Running;
                    controlButton.setImageResource(R.drawable.ic_pause_44dp);
                }
            }
        });

        //Settings Button
        settingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent data = new Intent(MainActivity.this,SettingsActivity.class);
                startActivityForResult(data,THEME_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        bindService(timerIntent, timerConnection, Context.BIND_AUTO_CREATE);

        //Connect Components
        timerService.connectComponents(MainActivity.this, timerView);
    }

    @Override
    public void onResume(){
        super.onResume();
        //TODO: remove notification, remove background timer, update clock
        if(timerStatus == TimerState.Running){
            timer.startTimer();
            timer.removeTimerAlarm(this);
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        if(timerStatus == TimerState.Running){
            //TODO: start background service & notification
            timer.pauseTimer();
            timer.setTimerAlarm(this);

        } else if (timerStatus == TimerState.Paused){
            //TODO: show notification
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        unbindService(timerConnection);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == THEME_REQUEST_CODE){
            recreate();
        }
    }
}