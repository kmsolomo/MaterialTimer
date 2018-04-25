package com.example.admin.materialtimer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

/**
 * Created by admin on 4/19/18.
 */

public class NotificationUtil {

    public static final String ACTION_START = "start";
    public static final String ACTION_PAUSE = "pause";
    public static final String ACTION_RESET = "reset";
    private final String CHANNEL_ID = "TIMER_CHANNEL_ID";
    private final String CHANNEL_NAME = "Timer";
    public static final int TIMER_ID = 0;
    private Context mainContext;
    private NotificationManager notificationManager;

    public NotificationUtil(Context context){
        mainContext = context;
        notificationManager = (NotificationManager) mainContext.getSystemService(mainContext.NOTIFICATION_SERVICE);
    }

    public  void setNotificationRunning(){

        //create intent
        Intent timerIntent = new Intent(mainContext,MainActivity.class);

        //add intent to task stack
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(mainContext);
        taskStackBuilder.addNextIntentWithParentStack(timerIntent);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        //build notification
        Notification.Builder builder = new Notification.Builder(mainContext);
        builder.setSmallIcon(R.drawable.ic_alarm)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentTitle("Testing Title")
                .setContentText("Content Text")
                .setCategory(Notification.CATEGORY_ALARM);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(notificationChannel);
            builder.setChannelId(CHANNEL_ID);
        }

        //display notification
        notificationManager.notify(TIMER_ID,builder.build());
    }

    public void hideTimer(){
        notificationManager.cancel(TIMER_ID);
    }
}
