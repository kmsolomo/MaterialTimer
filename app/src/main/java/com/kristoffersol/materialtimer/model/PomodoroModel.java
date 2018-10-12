/*
 * Copyright 2018 Kristoffer Solomon
 *
 * This file is part of MaterialTimer.
 *
 * MaterialTimer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialTimer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MaterialTimer.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kristoffersol.materialtimer.model;


import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.content.SharedPreferences;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.kristoffersol.materialtimer.R;

import static android.content.Context.VIBRATOR_SERVICE;

public class PomodoroModel {

    private enum TimerState{
        WORK,
        BREAK,
        LONG_BREAK
    }

    private String WORK_TIME;
    private String BREAK_TIME;
    private String LONG_BREAK_TIME;
    private String LOOP_AMOUNT_VALUE;
    private String VIBRATE;

    private MutableLiveData<String> currentTime;
    private MutableLiveData<Boolean> isRunning;
    private MutableLiveData<Boolean> sessionStart;
    private TimerState currentState;
    private Vibrator vibrator;

    private long milliSecondsLeft,workTime,breakTime,longBreakTime;
    private int sessionBeforeLongBreak,sessionCount;
    private CountDownTimer timer;
    private SharedPreferences sharedPref;

    public PomodoroModel(Context context){
        currentTime = new MutableLiveData<>();
        isRunning = new MutableLiveData<>();
        sessionStart = new MutableLiveData<>();
        isRunning.setValue(false);
        sessionStart.setValue(false);
        currentState = TimerState.WORK;
        sessionCount = 0;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            vibrator = context.getSystemService(Vibrator.class);
        } else {
            vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        }

        WORK_TIME = context.getResources().getString(R.string.WORK_KEY);
        BREAK_TIME = context.getResources().getString(R.string.BREAK_KEY);
        LONG_BREAK_TIME = context.getResources().getString(R.string.LONG_BREAK_KEY);
        LOOP_AMOUNT_VALUE = context.getResources().getString(R.string.LOOP_KEY);
        VIBRATE = context.getResources().getString(R.string.VIBRATE_KEY);


    }

    public LiveData<String> getTimer(){
        return currentTime;
    }

    public String getTime(){
        return currentTime.getValue();
    }

    public LiveData<Boolean> getState(){
        return isRunning;
    }

    public Boolean isRunning(){
        return isRunning.getValue();
    }

    public LiveData<Boolean> getSessionState(){
        return sessionStart;
    }

    private long convertTime(int value){
        return Long.valueOf(value) * 60000;
    }

    private String formatTime(long milliSecondsLeft){
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
        return currentTime;
    }

    public void refreshTimers(){
        workTime = convertTime(sharedPref.getInt(WORK_TIME,25));
        breakTime = convertTime(sharedPref.getInt(BREAK_TIME,5));
        longBreakTime = convertTime(sharedPref.getInt(LONG_BREAK_TIME,15));
        sessionBeforeLongBreak = sharedPref.getInt(LOOP_AMOUNT_VALUE,4);
        updateTimer(workTime);
    }

    public void startTimer(){
        if(currentState == TimerState.WORK && !sessionStart.getValue()){
            sessionStart.setValue(true);
            isRunning.setValue(true);
            timer(convertTime(sharedPref.getInt(WORK_TIME,25)));
        } else {
            isRunning.setValue(true);
            timer(milliSecondsLeft);
        }
    }

    public void pauseTimer(){
        timer.cancel();
        isRunning.setValue(false);
    }

    public void restartTimer(){
        restoreTimerState();
        if(isRunning()){
            isRunning.setValue(false);
            startTimer();
        }
    }

    /**
     * Reset timer to starting state
     */

    public void resetTimer(){
        pauseTimer();
        currentState = TimerState.WORK;
        sessionStart.setValue(false);
        refreshTimers();
    }

    public String getCurrentTimer(){
        if(currentState == TimerState.WORK){
            return "Work";
        } else if (currentState == TimerState.BREAK){
            return "Break";
        } else {
            return "Long Break";
        }
    }

    /**
     * Initialize new CountDownTimer that will loop through all timers
     * @param  timeLeft time to start countdown
     */

    private void timer(long timeLeft){
        timer = new CountDownTimer(timeLeft,300) {
            @Override
            public void onTick(long millisUntilFinished) {
                milliSecondsLeft = millisUntilFinished;
                updateTimer(milliSecondsLeft);
                saveTimerState();
            }

            @Override
            public void onFinish() {
                vibrate();
                switch (currentState){
                    case WORK:
                        if(sessionCount < sessionBeforeLongBreak){
                            sessionCount++;
                            currentState = TimerState.BREAK;
                            refreshTimers();
                            timer(breakTime);
                        } else {
                            sessionCount = 0;
                            currentState = TimerState.LONG_BREAK;
                            timer(longBreakTime);
                        }
                        break;
                    case BREAK:
                        currentState = TimerState.WORK;
                        refreshTimers();
                        timer(workTime);
                        break;
                    case LONG_BREAK:
                        currentState = TimerState.WORK;
                        refreshTimers();
                        timer(workTime);
                        break;
                    default:
                        break;
                }
            }
        }.start();
    }

    private void updateTimer(long milliSecondsLeft){
        currentTime.setValue(formatTime(milliSecondsLeft));
    }

    private void restoreTimerState(){
        sessionStart.setValue(sharedPref.getBoolean("sessionStart",false));
        isRunning.setValue(sharedPref.getBoolean("running",false));
        sessionCount = sharedPref.getInt("sessionCount",0);
        sessionBeforeLongBreak = sharedPref.getInt("sessionBeforeLongBreak",4);
        milliSecondsLeft = sharedPref.getLong("timeLeft",0);

        int timerState = sharedPref.getInt("currentTimer",0);

        if(timerState == 0){
            currentState = TimerState.WORK;
        } else if(timerState == 1){
            currentState = TimerState.BREAK;
        } else{
            currentState = TimerState.LONG_BREAK;
        }
    }

    private void saveTimerState(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("sessionStart",sessionStart.getValue());
        editor.putBoolean("running",isRunning.getValue());
        editor.putInt("sessionCount",sessionCount);
        editor.putInt("sessionBeforeLongBreak",sessionBeforeLongBreak);
        editor.putLong("timeLeft",milliSecondsLeft);

        if(currentState == TimerState.WORK){
            editor.putInt("currentTimer",0);
        } else if(currentState == TimerState.BREAK){
            editor.putInt("currentTimer",1);
        } else {
            editor.putInt("currentTimer",2);
        }
        editor.apply();
    }

    private void vibrate(){
        boolean vibratePref = sharedPref.getBoolean(VIBRATE,false);
        if(vibratePref){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                vibrator.vibrate(VibrationEffect.createOneShot(1000,VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(1000);
            }
        }
    }
}
