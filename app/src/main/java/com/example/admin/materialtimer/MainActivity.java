package com.example.admin.materialtimer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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
    private Intent timerIntent;
    private Messenger timerMessenger;
    private final int THEME_REQUEST_CODE = 1;
    public static final int UPDATE_TIME = 1;

    private ServiceConnection timerConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName className, IBinder service){
            timerMessenger = new Messenger(service);

            Messenger uiMessenger = new Messenger(new UIHandler(Looper.getMainLooper()));
            Message uiMsg= Message.obtain();
            uiMsg.what = TimerService.REGISTER_CLIENT;
            uiMsg.replyTo = uiMessenger;

            try {
                timerMessenger.send(uiMsg);
            } catch (RemoteException e){
                Log.v("RemoteException", e.toString());
            }
            Log.v("MainActivty","onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0){
            timerMessenger = null;
            Log.v("MainActivty","onServiceDisconnected");
        }
    };

    public final class UIHandler extends Handler {

        public UIHandler(Looper looper){
            super(looper);
        }
        @Override
        public void handleMessage (Message message){
            switch(message.what){
                case UPDATE_TIME:
                    String currentTime = (String) message.obj;
                    timerView.setText(currentTime);
                default:
                    super.handleMessage(message);
            }
        }
    }

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

                    Message msg = Message.obtain();
                    msg.what = TimerService.PAUSE_TIMER;
                    try{
                        timerMessenger.send(msg);
                        Log.v("onClick","PAUSE_TIMER");
                    } catch(RemoteException e) {
                        Log.v("RemoteException",e.toString());
                    }
                    timerStatus = TimerState.Paused;
                    controlButton.setImageResource(R.drawable.ic_play_arrow_44dp);
                } else {

                    Message msg = Message.obtain();
                    msg.what = TimerService.START_TIMER;
                    try{
                        timerMessenger.send(msg);
                        Log.v("onClick","START_TIMER");
                    } catch(RemoteException e) {
                        Log.v("RemoteException",e.toString());
                    }

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == THEME_REQUEST_CODE){
            recreate();
        }
    }
}