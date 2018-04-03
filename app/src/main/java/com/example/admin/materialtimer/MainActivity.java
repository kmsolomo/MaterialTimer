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
        themeCheck();
        setContentView(R.layout.activity_main);

        //Bind Views
        controlButton = findViewById(R.id.floatingActionButton);
        timerView = findViewById(R.id.timerTextView);
        settingsButton = findViewById(R.id.SettingsButton);

        initDefaultVal();
        //milliSecondsLeft = convertTime(timerVal);
        milliSecondsLeft = 10000;
        updateTimer(milliSecondsLeft);

        //Floating action button
        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timerStatus == TimerState.Running){

                    //TODO: Pause Timer

                    controlButton.setImageResource(R.drawable.ic_play_arrow_44dp);
                } else {

                    //TODO: Start Timer

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == THEME_REQUEST_CODE){
            recreate();
        }
    }

    public void startTimer(){

        timer = new CountDownTimer(milliSecondsLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                milliSecondsLeft = millisUntilFinished;
                updateTimer(milliSecondsLeft);
            }

            @Override
            public void onFinish() {
                startTimer();
            }
        }.start();
    }

    public void initDefaultVal(){
        //initialize default values
        timerView.setTextColor(getResources().getColor(R.color.textColorLight));
        controlButton.setImageResource(R.drawable.ic_play_arrow_44dp);
        settingsButton.setBackground(getResources().getDrawable(R.drawable.ic_settings_light_44dp));

        //Set Default Preferences
        PreferenceManager.setDefaultValues(getApplicationContext(),R.xml.preferences,false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        timerVal = sharedPref.getString(keyChain[1],"");
    }

    public long convertTime(String value){
        return Long.valueOf(value) * 60000;
    }

    public void themeCheck(){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        appTheme = sharedPref.getString(keyChain[0],defaultTheme);

        switch(appTheme){
            case "Light":
                setTheme(R.style.LightAppTheme);
                break;
            case "Dark":
                setTheme(R.style.DarkAppTheme);
                break;
            case "Black":
                setTheme(R.style.BlackAppTheme);
                break;
            default:
                break;
        }
    }

    public void updateTimer(long milliSecondsLeft){
        int minutes = (int) milliSecondsLeft / 60000;
        int seconds = (int) milliSecondsLeft % 60000 / 1000;
        String currentTime = "";

        if(minutes < 10){
            currentTime = "0" + minutes;
        } else {
            currentTime += minutes;
        }

        currentTime += ":";

        if(seconds < 10){
            currentTime += "0" + seconds;
        } else {
            currentTime += seconds;
        }

        timerView.setText(currentTime);
    }
}
