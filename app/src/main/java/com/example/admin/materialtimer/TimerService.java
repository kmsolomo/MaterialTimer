package com.example.admin.materialtimer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;



/**
 * Created by admin on 4/21/18.
 */

public class TimerService extends Service implements TimerInterface {

    public enum Timer{
        Work, Break, LongBreak
    }

    private final IBinder timerBinder = new TimerBinder();
    private TextView timerView;

    //TimerUtility
    private Timer timer;
    private Handler timerHandler;
    private HandlerThread workerThread;
    private Looper workerLooper;
    private long milliSecondsLeft,countDownInterval;
    private int sessionBeforeLongBreak,sessionCount;
    private boolean sessionStart,customFlag,connected,notification;
    private SharedPreferences sharedPref;
    private Runnable workRun,breakRun,longBreakRun;
    private CountDownTimer workTimer,breakTimer,longBreakTimer,customTimer;
    private NotificationUtil notify;

    private final String WORK_TIME = "pref_work_time";
    private final String BREAK_TIME = "pref_break_time";
    private final String LONG_BREAK_TIME = "pref_long_break_time";
    private final String LOOP_AMOUT_VALUE = "pref_loop_amount";
    private final String ALARM_SET_TIME = "com.example.admin.materialtimer";

    public class TimerBinder extends Binder {
        TimerService getService(){
            return TimerService.this;
        }
    }

    //Create Message Handler


    @Override
    public void onCreate(){
        super.onCreate();
        timer = Timer.Work;
        sessionStart = false;
        customFlag = false;
        connected = false;
        notification = false;
        sessionCount = 0;
        countDownInterval = 300;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        notify = new NotificationUtil(this);

        workerThread = new HandlerThread("WorkerThread", Process.THREAD_PRIORITY_BACKGROUND);
        workerThread.start();
        workerLooper = workerThread.getLooper();
        timerHandler = new Handler(workerLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent){
        connected = true;
        Log.v("TimerService","onBind");
        return timerBinder;
    }

    @Override
    public boolean onUnbind(Intent intent){
        connected = false;
        Log.v("TimerService","onUnbind");
        return true;
    }

    @Override
    public void onRebind(Intent intent){
        connected = true;
        Log.v("TimerService","onRebind");
        super.onRebind(intent);
    }

    public void connectComponent(TextView view){
        timerView = view;
        refreshTimers();
    }

    public void disconnectComponent(){
        timerView = null;
    }

    public void startTimer(){
        if(timer == Timer.Work && !sessionStart){
            sessionStart = true;
            timerHandler.post(workRun);
        } else {
            startCustomTimer(getTime());
        }
    }

    public void pauseTimer(){
        if(customFlag){
            saveTime();
            customTimer.cancel();
        } else {
            switch(timer){
                case Work:
                    saveTime();
                    workTimer.cancel();
                    break;
                case Break:
                    saveTime();
                    breakTimer.cancel();
                    break;
                case LongBreak:
                    saveTime();
                    longBreakTimer.cancel();
                    break;
                default:
                    break;
            }
        }
    }

    public void saveTime(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong("timeLeft",milliSecondsLeft);
        editor.apply();
    }

    public long getTime(){
        return sharedPref.getLong("timeLeft",0);
    }

//    public void saveAlarmTime(){
//
//    }
//
//    public long getAlarmTime(){
//        return sharedPref.getLong(ALARM_SET_TIME,0);
//    }

    public void resetTimer(){
//        timer = Timer.Work;
//        sessionStart = false;
//        customFlag = false;
//        refreshTimers();
        notify.hideTimer();
        workerThread.quit();
        stopSelf();
    }

    public long convertTime(int value){
        return Long.valueOf(value) * 60000;
    }

    public void startCustomTimer(long timeLeft){
        customFlag = true;
        customTimer = new CountDownTimer(timeLeft,countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                milliSecondsLeft = millisUntilFinished;
                updateTimer(milliSecondsLeft);
            }

            @Override
            public void onFinish() {
                customFlag = false;
                switch (timer){
                    case Work:
                        if(sessionCount < sessionBeforeLongBreak){
                            sessionCount++;
                            timerHandler.post(breakRun);
                        } else {
                            sessionCount = 0;
                            timerHandler.post(longBreakRun);
                        }
                        break;
                    case Break:
                        timerHandler.post(workRun);
                        break;
                    case LongBreak:
                        timerHandler.post(workRun);
                        break;
                    default:
                        break;
                }

            }
        }.start();

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

        if(connected){

            if(notification){
                notification = false;
                notify.hideTimer();
            }

            timerView.setText(currentTime);
            Log.v("TimerService","textView");
        } else {
            notification = true;
            notify.post(currentTime, MainActivity.TimerState.Running);
            Log.v("TimerService","notification post");
        }

    }

    public void refreshTimers(){
        //TODO: Get true values from shared preferences
        final long workTime = 60000;
        final long breakTime = 30000;
        final long longBreakTime = 40000;

//      final long workTime = convertTime(sharedPref.getInt(WORK_TIME,25));
//      final long breakTime = convertTime(sharedPref.getInt(BREAK_TIME,5));
//      final long longBreakTime = convertTime(sharedPref.getLong(LONG_BREAK_TIME,15));
        sessionBeforeLongBreak = sharedPref.getInt(LOOP_AMOUT_VALUE,4);

        //Issue with CountDownTimer implementation
        //CountDownInterval < 1000 as work around
        workRun = new Runnable(){
            @Override
            public void run(){
                timer = Timer.Work;
                workTimer = new CountDownTimer(workTime,countDownInterval) {
                    @Override
                    public void onTick(long l) {
                        milliSecondsLeft = l;
                        updateTimer(milliSecondsLeft);
                    }

                    @Override
                    public void onFinish() {
                        if(sessionCount < sessionBeforeLongBreak){
                            sessionCount++;
                            timerHandler.post(breakRun);
                        } else {
                            sessionCount = 0;
                            timerHandler.post(longBreakRun);
                        }
                    }
                }.start();
            }
        };

        breakRun = new Runnable() {
            @Override
            public void run() {
                timer = Timer.Break;
                breakTimer = new CountDownTimer(breakTime,countDownInterval) {
                    @Override
                    public void onTick(long l) {
                        milliSecondsLeft = l;
                        updateTimer(milliSecondsLeft);
                    }

                    @Override
                    public void onFinish() {
                        timerHandler.post(workRun);
                    }
                }.start();
            }
        };

        longBreakRun = new Runnable(){
            @Override
            public void run() {
                timer = Timer.LongBreak;
                longBreakTimer = new CountDownTimer(longBreakTime,countDownInterval) {
                    @Override
                    public void onTick(long l) {
                        milliSecondsLeft = l;
                        updateTimer(milliSecondsLeft);
                    }

                    @Override
                    public void onFinish() {
                        timerHandler.post(workRun);
                    }
                }.start();
            }
        };
    }
}