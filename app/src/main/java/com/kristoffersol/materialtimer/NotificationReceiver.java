package com.kristoffersol.materialtimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        if(intent.getAction() != null){
            if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                Intent notificationIntent = new Intent(context, TimerService.class);
                notificationIntent.setAction(TimerService.SCREEN_OFF);
                context.startService(notificationIntent);
            }
        }
    }
}
