package com.example.admin.materialtimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by admin on 5/14/18.
 */

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        if(intent.getAction() != null){
            if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                Intent startNotification = new Intent(context, TimerService.class);
                startNotification.setAction(TimerService.SCREEN_OFF);
                context.startService(startNotification);
            }
        }
    }
}
