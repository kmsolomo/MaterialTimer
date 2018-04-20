package com.example.admin.materialtimer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

/**
 * Created by admin on 4/19/18.
 */

public class NotificationUtil {

    public static final String ACTION_START = "start";
    public static final String ACTION_PAUSE = "pause";
    public static final String ACTION_RESET = "reset";
    public static final int TIMER_ID = 0;

    public NotificationUtil(){

    }

    protected void setNotificationRunning(Context context){

        //create intent
        Intent timerIntent = new Intent(context,MainActivity.class);

        //add intent to task stack
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(timerIntent);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        //build notification
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.ic_alarm)
                .setAutoCancel(true)
                .setDefaults(0)
                .setContentIntent(pendingIntent)
                .setContentTitle("Testing Title")
                .setContentText("Content Text");

        //display notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(TIMER_ID,builder.build());
    }

    protected void hideTimer(Context context){

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.cancel(TIMER_ID);
    }
}
