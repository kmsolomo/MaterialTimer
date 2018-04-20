package com.example.admin.materialtimer;

import android.content.Intent;
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

        milliSecondsLeft = 60000;
        timer = new TimerUtility(timerView,this);
        timer.updateTimer(milliSecondsLeft);

        notificationUtil = new NotificationUtil();

        //Floating action button
        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timerStatus == TimerState.Running){
                    timer.pauseTimer();
                    timerStatus = TimerState.Paused;
                    controlButton.setImageResource(R.drawable.ic_play_arrow_44dp);
                } else {
                    timer.startTimer();
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
    public void onResume(){
        super.onResume();
        //TODO: remove notification, remove background timer, update clock
        if(timerStatus == TimerState.Running){
            timer.startTimer();
            timer.removeTimerAlarm(this);
            notificationUtil.hideTimer(this);
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        if(timerStatus == TimerState.Running){
            //TODO: start background service & notification
            timer.pauseTimer();
            timer.setTimerAlarm(this);
            notificationUtil.setNotificationRunning(this);

        } else if (timerStatus == TimerState.Paused){
            //TODO: show notification
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        timer.removeTimerAlarm(this );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == THEME_REQUEST_CODE){
            recreate();
        }
    }
}