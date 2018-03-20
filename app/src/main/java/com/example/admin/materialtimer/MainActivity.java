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

    private FloatingActionButton controlButton;
    private ImageButton settingsButton;
    private TextView timerView;
    private int clickControl;
    private SharedPreferences sharedPref;
    private String themeKey = "pref_theme_value";
    private String defaultTheme = "Dark";
    private String appTheme;
    private final int THEME_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themeCheck();
        setContentView(R.layout.activity_main);

        //Bind Views
        clickControl = 0;
        controlButton = findViewById(R.id.floatingActionButton);
        timerView = findViewById(R.id.timerTextView);
        settingsButton = findViewById(R.id.imageButton);

        //Set Default Preferences
        PreferenceManager.setDefaultValues(getApplicationContext(),R.xml.preferences,false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String timerVal = sharedPref.getString("pref_work_time","");


        //initialize default values
        timerView.setText(timerVal);
        controlButton.setImageResource(R.drawable.ic_play_arrow_44dp);
        //controlButton.setBackgroundColor(getResources().getColor(R.color.textColorLight));

        //Create Work Timer
        final CountDownTimer firstTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                long timeInSeconds = l / 1000;
                String currentTime = String.valueOf(timeInSeconds);
                timerView.setText(currentTime);
            }

            @Override
            public void onFinish() {
                timerView.setText("0");
            }
        };

        //Create Break Timer
        final CountDownTimer secondTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                long timeInSeconds = l / 1000;
                String currentTime = String.valueOf(timeInSeconds);
                timerView.setText(currentTime);
            }

            @Override
            public void onFinish() {
                timerView.setText("0");
            }
        };

        //Floating action button
        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(clickControl == 1){
                    controlButton.setImageResource(R.drawable.ic_play_arrow_44dp);
                    clickControl = 0;

                } else {
                    controlButton.setImageResource(R.drawable.ic_pause_44dp);
                    clickControl = 1;
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

    public void themeCheck(){
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        appTheme = sharedPref.getString(themeKey,defaultTheme);

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
}
