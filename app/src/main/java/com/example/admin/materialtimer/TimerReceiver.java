package com.example.admin.materialtimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by admin on 4/16/18.
 */

public class TimerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        if(intent.getAction() != null){
            switch(intent.getAction()){
                case TimerService.TIMER_RESTART:
                    Intent restartIntent = new Intent(context,TimerService.class);
                    restartIntent.setAction(TimerService.TIMER_RESTART);
                    context.startService(restartIntent);
                    break;
                case TimerService.ACTION_PAUSE:
                    Intent pauseIntent = new Intent(context, TimerService.class);
                    pauseIntent.setAction(TimerService.ACTION_PAUSE);
                    context.startService(pauseIntent);
                    break;
                case TimerService.ACTION_START:
                    Intent startIntent = new Intent(context, TimerService.class);
                    startIntent.setAction(TimerService.ACTION_START);
                    context.startService(startIntent);
                    break;
                case TimerService.ACTION_RESET:
                    Intent resetIntent = new Intent(context, TimerService.class);
                    resetIntent.setAction(TimerService.ACTION_RESET);
                    context.startService(resetIntent);
                    break;
            }

        }
    }
}
