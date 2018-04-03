package com.example.admin.materialtimer;

import android.content.Context;
import android.widget.TextView;

/**
 * Created by admin on 4/3/18.
 */

public class TimerUtility {

    public static long converTime(String value){
        return Long.valueOf(value) * 60000;
    }

    public static void updateTimer(long milliSecondsLeft, TextView view){
        int minutes = (int) milliSecondsLeft / 60000;
        int seconds = (int) milliSecondsLeft % 60000 / 1000;
        String currentTime = "";

        if(minutes < 10){
            currentTime = "0" + minutes;
        } else {
            currentTime += minutes;
        }

        currentTime += ":";

        if(seconds < 10){
            currentTime += "0" + seconds;
        } else {
            currentTime += seconds;
        }

        view.setText(currentTime);
    }
}
