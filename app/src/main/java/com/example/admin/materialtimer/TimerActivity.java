package com.example.admin.materialtimer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

public class TimerActivity extends Activity{

    public enum TimerState {
        Running, Stopped, Paused
    }

    public static final int UPDATE_TIME = 1;
    public static final int UPDATE_STATE = 2;

    private FloatingActionButton controlButton,stopButton;
    private ImageButton settingsButton;
    private TextView timerView;
    private TimerState timerStatus = TimerState.Stopped;
    private Intent timerIntent;
    private Messenger timerMessenger;
    private BroadcastReceiver notificationReceiver;
    private boolean animation = false;
    private AnimatorSet animatorOut,animatorIn;
    private final int THEME_REQUEST_CODE = 1;

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

        try {
            timerMessenger.send(uiMsg);
        } catch (RemoteException e){
            Log.v("RemoteException", e.toString());
        }
    }

    private void stateUpdate(boolean state){
        if(timerStatus != TimerState.Stopped){
            if(state){
                timerStatus = TimerState.Running;
                controlButton.setImageResource(R.drawable.ic_pause_24dp);
            } else {
                timerStatus = TimerState.Paused;
                controlButton.setImageResource(R.drawable.ic_play_arrow_24dp);
            }
        }

    }

    private void initAnimations(float initialX){

        //Move to started position
        ObjectAnimator slideOut = ObjectAnimator.ofFloat(controlButton,"translationX",initialX-100f);
        slideOut.setDuration(500);
        slideOut.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(stopButton,"alpha",0,1);
        fadeIn.setDuration(500);
        fadeIn.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator stopButtonMoveOut = ObjectAnimator.ofFloat(stopButton,"translationX",initialX+100f);
        stopButtonMoveOut.setDuration(500);
        stopButtonMoveOut.setInterpolator(new AccelerateDecelerateInterpolator());

        //Move back to original position
        ObjectAnimator slideIn = ObjectAnimator.ofFloat(controlButton,"translationX",initialX);
        slideIn.setDuration(500);
        slideIn.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator stopButtonFadeOut = ObjectAnimator.ofFloat(stopButton,"alpha",1,0);
        stopButtonFadeOut.setDuration(500);
        stopButtonFadeOut.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator stopButtonMoveIn = ObjectAnimator.ofFloat(stopButton,"translationX",initialX);
        stopButtonFadeOut.setDuration(500);
        stopButtonFadeOut.setInterpolator(new AccelerateDecelerateInterpolator());

        //Setup animation sequence
        animatorOut = new AnimatorSet();
        animatorOut.addListener(animOutListener);
        animatorOut.playTogether(slideOut,fadeIn,stopButtonMoveOut);

        animatorIn = new AnimatorSet();
        animatorIn.addListener(animInListener);
        animatorIn.playTogether(slideIn,stopButtonFadeOut,stopButtonMoveIn);
    }

    private View.OnClickListener playPauseListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            if(timerStatus == TimerState.Running){
                Message msg = Message.obtain();
                msg.what = TimerService.PAUSE_TIMER;
                try{
                    timerMessenger.send(msg);
                    Log.v("onClick","PAUSE_TIMER");
                } catch(RemoteException e) {
                    Log.e("RemoteException",e.toString());
                }
                timerStatus = TimerState.Paused;
                controlButton.setImageResource(R.drawable.ic_play_arrow_24dp);
            } else {
                Message msg = Message.obtain();
                msg.what = TimerService.START_TIMER;
                try{
                    timerMessenger.send(msg);
                    Log.v("onClick","START_TIMER");
                } catch(RemoteException e) {
                    Log.e("RemoteException",e.toString());
                }

                if(!animation){
                    animatorOut.start();
                    animation = true;
                }
                timerStatus = TimerState.Running;
                controlButton.setImageResource(R.drawable.ic_pause_24dp);
            }
        }
    };

    private View.OnClickListener stopButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            Message stopMsg = Message.obtain();
            stopMsg.what = TimerService.RESET_TIMER;
            try{
                timerMessenger.send(stopMsg);
            } catch(RemoteException e){
                Log.e("RemoteException",e.toString());
            }
            animatorIn.start();
            animation = false;
            timerStatus = TimerState.Stopped;
            controlButton.setImageResource(R.drawable.ic_play_arrow_24dp);
            }
    };

    private View.OnClickListener settingsListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            Intent data = new Intent(TimerActivity.this,SettingsActivity.class);
            startActivityForResult(data,THEME_REQUEST_CODE);
        }
    };

    private Animator.AnimatorListener animOutListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {
            stopButton.setVisibility(View.VISIBLE);
            Log.v("animatorOutListener","set Visible");
        }
        @Override
        public void onAnimationEnd(Animator animator) { }
        @Override
        public void onAnimationCancel(Animator animator) { }
        @Override
        public void onAnimationRepeat(Animator animator) { }
    };

    private Animator.AnimatorListener animInListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) { }
        @Override
        public void onAnimationEnd(Animator animator) {
            stopButton.setVisibility(View.GONE);
        }
        @Override
        public void onAnimationCancel(Animator animator) { }
        @Override
        public void onAnimationRepeat(Animator animator) { }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ThemeUtility.themeCheck(this);
        setContentView(R.layout.activity_main);

        //Bind Views
        controlButton = findViewById(R.id.playPauseButton);
        stopButton = findViewById(R.id.stopButton);
        timerView = findViewById(R.id.timerTextView);
        settingsButton = findViewById(R.id.SettingsButton);

        //TODO: Animation state view position
        //Restore state
        if(savedInstanceState != null){
            if(savedInstanceState.get("TIMER_STATE") == TimerState.Running){
                timerStatus = TimerState.Running;
                controlButton.setImageResource(R.drawable.ic_pause_24dp);
            } else if(savedInstanceState.get("TIMER_STATE") == TimerState.Paused){
                timerStatus = TimerState.Paused;
                controlButton.setImageResource(R.drawable.ic_play_arrow_24dp);
            }
            timerView.setText(savedInstanceState.getString("CURRENT_TIME"));
            //get animation state
            Log.v("TimerActivity","onCreate restoring state");
        }

        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);

        //insures service persists bound lifecycle
        timerIntent = new Intent(TimerActivity.this, TimerService.class);
        startService(timerIntent);

        //Broadcast receiver to send notification when screen off
        notificationReceiver = new NotificationReceiver();
        IntentFilter notificationFilter = new IntentFilter();
        notificationFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(notificationReceiver,notificationFilter);

        //link OnClickListeners
        controlButton.setOnClickListener(playPauseListener);
        stopButton.setOnClickListener(stopButtonListener);
        settingsButton.setOnClickListener(settingsListener);

        initAnimations(controlButton.getTranslationX());
    }

    @Override
    protected void onStart(){
        super.onStart();
        bindService(timerIntent, timerConnection, Context.BIND_AUTO_CREATE);
        Log.v("TimerActivity","onStart()");
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

        //save view state

        Log.v("TimerActivity","onSaveInstanceState");
    }

    @Override
    protected void onStop(){
        super.onStop();
        unbindService(timerConnection);
        Log.v("TimerActivity","onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(notificationReceiver);
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
        if(timerStatus != TimerState.Stopped){
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
}