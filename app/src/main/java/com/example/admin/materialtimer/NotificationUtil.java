package com.example.admin.materialtimer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
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

    public void post(String time, MainActivity.TimerState state){

        Notification.Builder builder;
        Notification.Action pauseAction;
        Notification.Action stopAction;

        //Intent sent when notification clicked
        Intent timerIntent = new Intent(mainContext,MainActivity.class);
        timerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        timerIntent.putExtra("TIMER_STATE",state);
        PendingIntent timerPendingIntent = PendingIntent.getActivity(mainContext,0,timerIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        //add intent to task stack
        //TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(mainContext);
        //taskStackBuilder.addNextIntentWithParentStack(timerIntent);
        //PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        //Intent to pause timer
        Intent pauseIntent = new Intent(mainContext, TimerReceiver.class);
        pauseIntent.setAction(ACTION_PAUSE);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(mainContext,0,pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT);
//
//        //Intent to start timer
//        Intent startIntent = new Intent(mainContext, TimerReceiver.class);
//        startIntent.setAction(ACTION_START);
//        PendingIntent startPendingIntent = PendingIntent.getBroadcast(mainContext, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Intent to stop timer
        Intent stopIntent = new Intent(mainContext, TimerReceiver.class);
        stopIntent.setAction(ACTION_RESET);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(mainContext,0,stopIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            Icon pauseIcon = Icon.createWithResource(mainContext, R.drawable.ic_alarm);
            pauseAction = new Notification.Action.Builder(pauseIcon, "PAUSE", pausePendingIntent).build();
            stopAction = new Notification.Action.Builder(pauseIcon, "STOP", stopPendingIntent).build();

        } else {
            pauseAction = new Notification.Action.Builder(R.drawable.ic_alarm, "PAUSE", pausePendingIntent).build();
            stopAction = new Notification.Action.Builder(R.drawable.ic_alarm, "STOP", stopPendingIntent).build();
        }

        builder = new Notification.Builder(mainContext);
        builder.setSmallIcon(R.drawable.ic_alarm)
                .setAutoCancel(true)
                .setContentIntent(timerPendingIntent)
                .setContentTitle("Testing Title")
                .setContentText(time)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .addAction(pauseAction)
                .addAction(stopAction)
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
