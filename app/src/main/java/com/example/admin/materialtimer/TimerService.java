package com.example.admin.materialtimer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.TextView;


/**
 * Created by admin on 4/21/18.
 */

public class TimerService extends Service implements TimerInterface {

    private final IBinder timerBinder = new TimerBinder();
    private TextView timerView;
    private Context currentContext;

    public class TimerBinder extends Binder {
        TimerService getService(){
            return TimerService.this;
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent){
        return timerBinder;

    }

    public void connectComponents(Context context, TextView view){
        currentContext = context;
        timerView = view;
    }

    public void disconnectComponents(){
        currentContext = null;
        timerView = null;
    }

    public void startTimer(){

    }

    public void pauseTimer(){

    }

    public void saveTime(){

    }

    public long getTime(){

    }

    public void saveAlarmTime(){

    }

    public long getAlarmTime(){

    }

    public void resetTimer(){

    }

    public void startCustomTimer(long timeLeft){

    }

    public void updateTimer(long milliSecondsLeft){
        int minutes = (int) milliSecondsLeft / 60000;
        int seconds = (int) milliSecondsLeft % 60000 / 1000;
        String currentTime = "";

        if (minutes < 10) {
            currentTime = "0" + minutes;
        } else {
            currentTime += minutes;
        }

        currentTime += ":";

        if (seconds < 10) {
            currentTime += "0" + seconds;
        } else {
            currentTime += seconds;
        }

        if(bound == true){
            timerView.setText(currentTime);
        } else {
            //TODO: Send to notifcation
        }

    }

    public void refreshTimers(){

    }



}