package com.example.admin.materialtimer;

/**
 * Created by admin on 4/24/18.
 */

public interface TimerInterface {

    void startTimer();
    void pauseTimer();
    void saveTime();
    long getTime();
//    void saveAlarmTime();
//    long getAlarmTime();
    void resetTimer();
    long convertTime(int value);
    void startCustomTimer(long timeLeft);
    void updateTimer(long milliSecondsLeft);
    void refreshTimers();

}
