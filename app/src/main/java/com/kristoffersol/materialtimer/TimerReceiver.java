package com.kristoffersol.materialtimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class TimerReceiver extends BroadcastReceiver {

    private void startService(Context context, Intent intent){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    /**
     * Interface with TimerService
     */

    @Override
    public void onReceive(Context context, Intent intent){
        if(intent.getAction() != null){
            switch(intent.getAction()){
                case TimerService.ACTION_PAUSE:
                    Intent pauseIntent = new Intent(context, TimerService.class);
                    pauseIntent.setAction(TimerService.ACTION_PAUSE);
                    startService(context,pauseIntent);
                    break;
                case TimerService.ACTION_START:
                    Intent startIntent = new Intent(context, TimerService.class);
                    startIntent.setAction(TimerService.ACTION_START);
                    startService(context,startIntent);
                    break;
                case TimerService.ACTION_RESET:
                    Intent resetIntent = new Intent(context, TimerService.class);
                    resetIntent.setAction(TimerService.ACTION_RESET);
                    startService(context,resetIntent);
                    break;
            }
        }
    }
}
