package com.example.admin.materialtimer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
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

    private SharedPreferences sharedPref;
    private CountDownTimer timer;
    private String [] keyChain = {"pref_theme_value","pref_work_time","pref_break_time",
            "pref_long_break_time","pref_loop_amount"};
    private String defaultTheme = "Dark";
    private String appTheme,timerVal;
    private final int THEME_REQUEST_CODE = 1;
    private TimerState timerStatus = TimerState.Stopped;
    private long milliSecondsLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtility.themeCheck(this);
        setContentView(R.layout.activity_main);

        //Bind Views
        controlButton = findViewById(R.id.floatingActionButton);
        timerView = findViewById(R.id.timerTextView);
        settingsButton = findViewById(R.id.SettingsButton);

        initDefaultVal();
        //milliSecondsLeft = convertTime(timerVal);
        milliSecondsLeft = 10000;
        TimerUtility.updateTimer(milliSecondsLeft,timerView);

        //Floating action button
        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timerStatus == TimerState.Running){

                    //TODO: Pause Timer
                    stopTimer();
                    timerStatus = TimerState.Paused;
                    controlButton.setImageResource(R.drawable.ic_play_arrow_44dp);
                } else {
                    //TODO: Start Timer
                    startTimer(milliSecondsLeft);
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

        milliSecondsLeft = sharedPref.getLong("TimeLeft",0);

        restartTimer();
        //TODO: remove notification, remove background timer, update clock
    }

    @Override
    public void onPause(){
        super.onPause();

        if(timerStatus == TimerState.Running){
            timer.cancel();

            //TODO: start background service & notification

        } else if (timerStatus == TimerState.Paused){

            //TODO: show notification
        }

        //Save Data
        //Testing
        sharedPref.edit().putLong("TimeLeft",milliSecondsLeft).apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == THEME_REQUEST_CODE){
            recreate();
        }
    }

    public void restartTimer(){

    }

    public void startTimer(long timeLeft){

        timer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                milliSecondsLeft = millisUntilFinished;
                TimerUtility.updateTimer(milliSecondsLeft,timerView);
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    public void stopTimer(){
        timer.cancel();
    }

    public void initDefaultVal(){
        //Set Default Preferences
        PreferenceManager.setDefaultValues(getApplicationContext(),R.xml.preferences,false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        timerVal = sharedPref.getString(keyChain[1],"");
    }
}
