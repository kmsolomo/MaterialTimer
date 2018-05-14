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

public class TimerActivity extends Activity{

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
    public static final int UPDATE_STATE = 2;

    private ServiceConnection timerConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName className, IBinder service){
            timerMessenger = new Messenger(service);
            synchronizeService();
            Log.v("MainActivty","onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0){
            timerMessenger = null;
            Log.v("MainActivty","onServiceDisconnected");
        }
    };

    private final class UIHandler extends Handler {

        public UIHandler(Looper looper){
            super(looper);
        }
        @Override
        public void handleMessage (Message message){
            switch(message.what){
                case UPDATE_TIME:
                    String currentTime = (String) message.obj;
                    timerView.setText(currentTime);
                    break;
                case UPDATE_STATE:
                    boolean state = (boolean) message.obj;
                    stateUpdate(state);
                    break;
                default:
                    super.handleMessage(message);
            }
        }
    }

    private void synchronizeService(){
        //Connect Client to Service
        Messenger uiMessenger = new Messenger(new UIHandler(Looper.getMainLooper()));

        Message uiMsg= Message.obtain();
        uiMsg.what = TimerService.REGISTER_CLIENT;
        uiMsg.replyTo = uiMessenger;

//        Message syncMsg = Message.obtain();
//        syncMsg.what = TimerService.SYNC_CLIENT;

        try {
            timerMessenger.send(uiMsg);
//            timerMessenger.send(syncMsg);
        } catch (RemoteException e){
            Log.v("RemoteException", e.toString());
        }
    }

    private void stateUpdate(boolean state){
        if(state){
            timerStatus = TimerState.Running;
            controlButton.setImageResource(R.drawable.ic_pause_44dp);
        } else {
            timerStatus = TimerState.Paused;
            controlButton.setImageResource(R.drawable.ic_play_arrow_44dp);
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
        timerIntent = new Intent(TimerActivity.this, TimerService.class);
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
            timerView.setText(savedInstanceState.getString("CURRENT_TIME"));
            Log.v("TimerActivity","onCreate restoring state");
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
                Intent data = new Intent(TimerActivity.this,SettingsActivity.class);
                startActivityForResult(data,THEME_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        bindService(timerIntent, timerConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        if(timerStatus == TimerState.Running){
            outState.putSerializable("TIMER_STATE",timerStatus);
        } else if (timerStatus == TimerState.Paused){
            outState.putSerializable("TIMER_STATE",timerStatus);
        }
        outState.putString("CURRENT_TIME",timerView.getText().toString());
        Log.v("TimerActivity","onSaveInstanceState");
}

    @Override
    protected void onStop(){
        super.onStop();
        unbindService(timerConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("TimerActivity","onDestory()");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == THEME_REQUEST_CODE){
            recreate();
        }
    }

    @Override
    protected void onUserLeaveHint(){
        Message notifyMsg = Message.obtain();
        notifyMsg.what = TimerService.START_NOTIFICATION;
            try{
                timerMessenger.send(notifyMsg);
            } catch (RemoteException e){
                Log.e("RemoteException",e.toString());
            }
        Log.v("onUserLeaveHint()","notification message sent");
    }
}