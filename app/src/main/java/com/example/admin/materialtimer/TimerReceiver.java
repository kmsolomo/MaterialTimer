package com.example.admin.materialtimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

/**
 * Created by admin on 4/16/18.
 */

public class TimerReceiver extends BroadcastReceiver {

    public static final String ACTION_START = "start";
    public static final String ACTION_PAUSE = "pause";
    public static final String ACTION_RESET = "reset";

    @Override
    public void onReceive(Context context, Intent intent){
//
//        TimerService timerService = (TimerService) context;
//
//        if(intent.getAction().equals(ACTION_PAUSE)){
//            timerService.pauseTimer();
//
//        } else if(intent.getAction().equals(ACTION_RESET)){
//            timerService.resetTimer();
//        }

        //Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        //vibrator.vibrate(1000);
    }
}
