package com.example.admin.materialtimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * Created by admin on 4/3/18.
 */

public class TimerUtility {

    public enum Timer {
        Work, Break, LongBreak, Remaining
    }

    private Timer timer;
    private TextView timerView;
    private Handler timerHandler;
    private long milliSecondsLeft;
    private int sessionBeforeLongBreak,sessionCount;
    private boolean sessionStart,customFlag;
    private SharedPreferences sharedPref;
    private Runnable workRun,breakRun,longBreakRun;
    private CountDownTimer workTimer,breakTimer,longBreakTimer,customTimer;

    private final String THEME_VALUE = "pref_theme_value";
    private final String WORK_TIME = "pref_work_time";
    private final String BREAK_TIME = "pref_break_time";
    private final String LONG_BREAK_TIME = "pref_long_break_time";
    private final String LOOP_AMOUT_VALUE = "pref_loop_amount";



    public TimerUtility(TextView view,Context activity){
        timerView = view;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        sessionCount = 0;
        timer = Timer.Work;
        sessionStart = false;
        customFlag = false;
        timerHandler = new Handler();
        refreshTimers();
    }

    public void startTimer(){
//        switch(timer){
//            case Work:
//                timerHandler.post(workRun);
//                break;
//            case Break:
//                timerHandler.post(breakRun);
//                break;
//            case LongBreak:
//                timerHandler.post(longBreakRun);
//                break;
//            default:
//                startCustomTimer(getTime());
//                break;
//        }
        if(timer == Timer.Work && sessionStart == false){
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

    public void resetTimer(){
        //TODO: stop current timer and restore fresh state
    }

    public long convertTime(int value){
        return Long.valueOf(value) * 60000;
    }

    public void startCustomTimer(long timeLeft){
        customFlag = true;
         customTimer = new CountDownTimer(timeLeft,300) {
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
                             timerHandler.post(longBreakRun);
                         }
                         break;
                     case Break:
                         timerHandler.post(workRun);
                         break;
                     case LongBreak:
                         sessionCount = 0;
                         timerHandler.post(workRun);
                         break;
                     default:
                         break;
                 }

             }
         }.start();
    }

    public void updateTimer(long milliSecondsLeft) {
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

        timerView.setText(currentTime);
    }

    public void refreshTimers(){

        //TODO: Get true values from shared preferences
        final long workTime = 10000;
        final long breakTime = 5000;
        final long longBreakTime = 7000;
        sessionBeforeLongBreak = sharedPref.getInt(LOOP_AMOUT_VALUE,4);

        //Issue with CountDownTimer implementation
        //CountDownInterval < 1000 as work around
        workRun = new Runnable(){
            @Override
            public void run(){
                timer = Timer.Work;
                workTimer = new CountDownTimer(workTime,300) {
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
                breakTimer = new CountDownTimer(breakTime,300) {
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
                longBreakTimer = new CountDownTimer(longBreakTime,300) {
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
