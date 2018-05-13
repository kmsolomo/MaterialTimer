package com.example.admin.materialtimer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Build;

/**
 * Created by admin on 4/19/18.
 */

public class NotificationUtil {

    private final String CHANNEL_ID = "TIMER_CHANNEL_ID";
    private final String CHANNEL_NAME = "Timer";
    private Context mainContext;
    private NotificationManager notificationManager;
    private Notification.Builder builder;
    private Notification.Action startAction;
    private Notification.Action pauseAction;
    private Notification.Action stopAction;

    public static final int NOTIFICATION_ID = 0;

    public NotificationUtil(Context context){
        mainContext = context;
        notificationManager = (NotificationManager) mainContext.getSystemService(mainContext.NOTIFICATION_SERVICE);
        initActions();
    }

    public void initActions(){

        //START TIMER
        Intent startIntent = new Intent(mainContext, TimerReceiver.class);
        startIntent.setAction(TimerService.ACTION_START);
        PendingIntent startPendingIntent = PendingIntent.getBroadcast(mainContext, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //PAUSE TIMER
        Intent pauseIntent = new Intent(mainContext, TimerReceiver.class);
        pauseIntent.setAction(TimerService.ACTION_PAUSE);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(mainContext,0,pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        //STOP TIMER
        Intent stopIntent = new Intent(mainContext, TimerReceiver.class);
        stopIntent.setAction(TimerService.ACTION_RESET);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(mainContext,0,stopIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            Icon pauseIcon = Icon.createWithResource(mainContext, R.drawable.ic_alarm);
            startAction = new Notification.Action.Builder(pauseIcon,"START",startPendingIntent).build();
            pauseAction = new Notification.Action.Builder(pauseIcon, "PAUSE", pausePendingIntent).build();
            stopAction = new Notification.Action.Builder(pauseIcon, "STOP", stopPendingIntent).build();
        } else {
            startAction = new Notification.Action.Builder(R.drawable.ic_alarm,"START",startPendingIntent).build();
            pauseAction = new Notification.Action.Builder(R.drawable.ic_alarm, "PAUSE", pausePendingIntent).build();
            stopAction = new Notification.Action.Builder(R.drawable.ic_alarm, "STOP", stopPendingIntent).build();
        }

    }

    public Notification buildNotification(String currentTime, boolean timerRunning){

        //notification clicked
        Intent timerIntent = new Intent(mainContext,TimerActivity.class);
        timerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent timerPendingIntent = PendingIntent.getActivity(mainContext,0,timerIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        builder = new Notification.Builder(mainContext);

        //Change available action options depending on state
        if(timerRunning){
            builder.setSmallIcon(R.drawable.ic_alarm)
                    .setAutoCancel(true)
                    .setContentIntent(timerPendingIntent)
                    .setContentTitle("Testing Title")
                    .setContentText(currentTime)
                    .setOnlyAlertOnce(true)
                    .setOngoing(true)
                    .addAction(pauseAction)
                    .addAction(stopAction)
                    .setCategory(Notification.CATEGORY_ALARM);
        } else {
            builder.setSmallIcon(R.drawable.ic_alarm)
                    .setAutoCancel(true)
                    .setContentIntent(timerPendingIntent)
                    .setContentTitle("Testing Title")
                    .setContentText(currentTime)
                    .setOnlyAlertOnce(true)
                    .setOngoing(true)
                    .addAction(startAction)
                    .addAction(stopAction)
                    .setCategory(Notification.CATEGORY_ALARM);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(notificationChannel);
            builder.setChannelId(CHANNEL_ID);
        }

        return builder.build();
    }

    public void updateNotification(String time){
        builder.setContentText(time);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }

    public void hideTimer(){
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
