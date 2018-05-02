package com.example.admin.materialtimer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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

    private TimerState timerStatus = TimerState.Stopped;
    private TimerService timerService;
    private Intent timerIntent;
    private final int THEME_REQUEST_CODE = 1;

    private ServiceConnection timerConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName className, IBinder service){
            TimerService.TimerBinder binder = (TimerService.TimerBinder) service;
            timerService = binder.getService();
            timerService.connectComponent(timerView);
            //TODO: Update textView on initial startup
            timerService.updateTimer(60000);
            Log.v("MainActivty","onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0){
            timerService.disconnectComponent();
            Log.v("MainActivty","onServiceDisconnected");
        }
    };

    private Handler handleUi = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage (Message message){

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
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

        //Restore state
        if(savedInstanceState != null){
            if(savedInstanceState.get("TIMER_STATE") == TimerState.Running){
                timerStatus = TimerState.Running;
                controlButton.setImageResource(R.drawable.ic_pause_44dp);
            } else if(savedInstanceState.get("TIMER_STATE") == TimerState.Paused){
                timerStatus = TimerState.Paused;
                controlButton.setImageResource(R.drawable.ic_play_arrow_44dp);
            }
            Log.v("MainActivity","onCreate restoring state");
        }

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
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState){
        if(savedInstanceState.get("TIMER_STATE") == TimerState.Running){
            timerStatus = TimerState.Running;
            controlButton.setImageResource(R.drawable.ic_pause_44dp);
        } else if(savedInstanceState.get("TIMER_STATE") == TimerState.Paused){
            timerStatus = TimerState.Paused;
            controlButton.setImageResource(R.drawable.ic_play_arrow_44dp);
        }
        Log.v("MainActivity","onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        if(timerStatus == TimerState.Running){
            outState.putSerializable("TIMER_STATE",timerStatus);
        } else if (timerStatus == TimerState.Paused){
            outState.putSerializable("TIMER_STATE",timerStatus);
        }
        Log.v("MainActivity","onSaveInstanceState");
    }

    @Override
    public void onStop(){
        super.onStop();
        unbindService(timerConnection);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("MainActivity","onDestory()");
    }

    @Override
    public void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        timerStatus = (TimerState) intent.getSerializableExtra("TIMER_STATUS");
        if(timerStatus == TimerState.Running){
            controlButton.setImageResource(R.drawable.ic_pause_44dp);
        }
        Log.v("MainActivity","onNewIntent called");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == THEME_REQUEST_CODE){
            recreate();
        }
    }
}