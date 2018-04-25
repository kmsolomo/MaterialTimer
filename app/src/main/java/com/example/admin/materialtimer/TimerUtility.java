package com.example.admin.materialtimer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

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
    private long milliSecondsLeft,countDownInterval;
    private int sessionBeforeLongBreak,sessionCount;
    private boolean sessionStart,customFlag;
    private SharedPreferences sharedPref;
    private Runnable workRun,breakRun,longBreakRun;
    private CountDownTimer workTimer,breakTimer,longBreakTimer,customTimer;

    private final String WORK_TIME = "pref_work_time";
    private final String BREAK_TIME = "pref_break_time";
    private final String LONG_BREAK_TIME = "pref_long_break_time";
    private final String LOOP_AMOUT_VALUE = "pref_loop_amount";
    private final String ALARM_SET_TIME = "com.example.admin.materialtimer";


    public TimerUtility(TextView view,Context activity){
        timerView = view;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
        timer = Timer.Work;
        sessionStart = false;
        customFlag = false;
        sessionCount = 0;
        countDownInterval = 300;
        timerHandler = new Handler();
        refreshTimers();
    }

    public void startTimer(){
        if(timer == Timer.Work && sessionStart == false){
            sessionStart = true;
            timerHandler.post(workRun);
        } else {
            long alarmSetTime = getAlarmTime();
            long currentTime = Calendar.getInstance().getTimeInMillis();
            long timeLeft = getTime();
            if(alarmSetTime > 0){
                timeLeft -= currentTime - alarmSetTime;
                startCustomTimer(timeLeft);
            } else {
                startCustomTimer(timeLeft);
            }
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

    public void saveAlarmTime(long currentTime){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(ALARM_SET_TIME,currentTime);
        editor.apply();
    }

    public long getAlarmTime(){
        return sharedPref.getLong(ALARM_SET_TIME,0);
    }

    public void resetTimer(){
        timer = Timer.Work;
        sessionStart = false;
        customFlag = false;
        refreshTimers();
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

    public void setTimerAlarm(Context context){
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long alarmTrigger = currentTime + milliSecondsLeft;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, TimerReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,1,intent,0);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTrigger, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,alarmTrigger,pendingIntent);
        }

        saveAlarmTime(currentTime);
    }

    public void removeTimerAlarm(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, TimerReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,1,intent,0);
        alarmManager.cancel(pendingIntent);
        saveAlarmTime(0);
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
