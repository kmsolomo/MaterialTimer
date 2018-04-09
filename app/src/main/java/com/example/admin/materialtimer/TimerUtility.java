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
        Work, Break, LongBreak
    }

    private Timer timer;
    private TextView timerView;
    private Handler timerHandler;
    private long milliSecondsLeft;
    private int sessionBeforeLongBreak,sessionCount;
    private SharedPreferences sharedPref;
    private Runnable workRun,breakRun,longBreakRun;
    private CountDownTimer workTimer,breakTimer,longBreakTimer;

    private final String [] keyChain = {"pref_theme_value","pref_work_time","pref_break_time",
            "pref_long_break_time","pref_loop_amount"};



    public TimerUtility(TextView view,Context activity){
        timerView = view;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        sessionBeforeLongBreak = 2;
        sessionCount = 0;
        timer = Timer.Work;
        timerHandler = new Handler();
        refreshTimers();
    }

    public void startTimer(){
        switch(timer){
            case Work:
                timerHandler.post(workRun);
                break;
            case Break:
                timerHandler.post(breakRun);
                break;
            case LongBreak:
                timerHandler.post(longBreakRun);
                break;
            default:
                break;
        }
    }

    public void pauseTimer(){
        //TODO: save timer state and ms left
        if(timer == Timer.Work){
            workTimer.cancel();
        } else if(timer == Timer.Break){
            breakTimer.cancel();
        } else {
            longBreakTimer.cancel();
        }
    }

    public void stopTimer(){
        //TODO: stop current timer and restore fresh state
    }

    public long convertTime(String value){
        return Long.valueOf(value) * 60000;
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
        final long workTime = 60000;
        final long breakTime = 30000;
        final long longBreakTime = 45000;

        //Initialize CountDownTimer Runnable
        //countDownInterval interval is < 1000 to counteract issue that skips onTick()
        workRun = new Runnable(){
            @Override
            public void run(){
                timer = Timer.Work;
                workTimer = new CountDownTimer(workTime,100) {
                    @Override
                    public void onTick(long l) {
                        updateTimer(l);
                    }

                    @Override
                    public void onFinish() {
                        if(sessionCount < sessionBeforeLongBreak){
                            timerHandler.post(breakRun);
                        } else {
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
                breakTimer = new CountDownTimer(breakTime,100) {
                    @Override
                    public void onTick(long l) {
                        updateTimer(l);
                    }

                    @Override
                    public void onFinish() {
                        sessionCount++;
                        timerHandler.post(workRun);
                    }
                }.start();
            }
        };

        longBreakRun = new Runnable(){
            @Override
            public void run() {
                longBreakTimer = new CountDownTimer(longBreakTime,100) {
                    @Override
                    public void onTick(long l) {
                        updateTimer(l);
                    }

                    @Override
                    public void onFinish() {
                        sessionCount = 0;
                        timerHandler.post(workRun);
                    }
                };
            }
        };

    }
}
