/*
 * Copyright 2018 Kristoffer Solomon
 *
 * This file is part of MaterialTimer.
 *
 * MaterialTimer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MaterialTimer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MaterialTimer.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kristoffersol.materialtimer.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Build;

import com.kristoffersol.materialtimer.R;
import com.kristoffersol.materialtimer.TimerActivity;
import com.kristoffersol.materialtimer.TimerReceiver;
import com.kristoffersol.materialtimer.PomodoroService;

public class NotificationUtil {

    private final String CHANNEL_ID = "TIMER_CHANNEL_ID";
    private final String CHANNEL_NAME = "Timer";
    private Context mainContext;
    private NotificationManager notificationManager;
    private Notification.Builder builder;
    private Notification.Action startAction;
    private Notification.Action pauseAction;
    private Notification.Action stopAction;

    public static final int NOTIFICATION_ID = 1;

    /**
     * initialize notification manager and create all actions for notifications
     * @param context provided by service
     */

    public NotificationUtil(Context context){
        mainContext = context;
        notificationManager = (NotificationManager) mainContext.getSystemService(Context.NOTIFICATION_SERVICE);

        //START TIMER
        Intent startIntent = new Intent(mainContext, TimerReceiver.class);
        startIntent.setAction(PomodoroService.ACTION_START);
        PendingIntent startPendingIntent = PendingIntent.getBroadcast(mainContext, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //PAUSE TIMER
        Intent pauseIntent = new Intent(mainContext, TimerReceiver.class);
        pauseIntent.setAction(PomodoroService.ACTION_PAUSE);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(mainContext,0,pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        //STOP TIMER
        Intent stopIntent = new Intent(mainContext, TimerReceiver.class);
        stopIntent.setAction(PomodoroService.ACTION_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(mainContext,0,stopIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            Icon startIcon = Icon.createWithResource(mainContext, R.drawable.ic_play_arrow_24dp);
            Icon pauseIcon = Icon.createWithResource(mainContext, R.drawable.ic_pause_24dp);
            Icon stopIcon = Icon.createWithResource(mainContext, R.drawable.ic_stop_24dp);
            startAction = new Notification.Action.Builder(startIcon,"START",startPendingIntent).build();
            pauseAction = new Notification.Action.Builder(pauseIcon, "PAUSE", pausePendingIntent).build();
            stopAction = new Notification.Action.Builder(stopIcon, "STOP", stopPendingIntent).build();
        } else {
            startAction = new Notification.Action.Builder(R.drawable.ic_play_arrow_24dp,"START",startPendingIntent).build();
            pauseAction = new Notification.Action.Builder(R.drawable.ic_pause_24dp, "PAUSE", pausePendingIntent).build();
            stopAction = new Notification.Action.Builder(R.drawable.ic_stop_24dp, "STOP", stopPendingIntent).build();
        }
    }

    /**
     * Construct the notification with necessary pending intents and actions
     * @param currentTime formatted time from to display
     * @param timerRunning current state of timer
     * @param timer current timer in loop
     */

    public Notification buildNotification(String currentTime, boolean timerRunning, String timer){

        //notification clicked
        Intent timerIntent = new Intent(mainContext,TimerActivity.class);
        timerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent timerPendingIntent = PendingIntent.getActivity(mainContext,0,timerIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        builder = new Notification.Builder(mainContext);

        builder.setSmallIcon(R.drawable.ic_alarm_24dp)
                .setAutoCancel(true)
                .setContentIntent(timerPendingIntent)
                .setContentTitle(timer)
                .setContentText(currentTime)
                .setOnlyAlertOnce(true)
                .setCategory(Notification.CATEGORY_ALARM);

        //Change available action options depending on state
        if(timerRunning){
            builder.addAction(pauseAction);
        } else {
            builder.addAction(startAction);
        }

        builder.addAction(stopAction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setLightColor(Color.BLUE);
            notificationManager.createNotificationChannel(notificationChannel);
            builder.setChannelId(CHANNEL_ID);
        }

        return builder.build();
    }

    /**
     * Updates the timer to reflect current state
     * @param time current time
     * @param timer current timer
     */

    public void updateNotification(String time,String timer){
        builder.setContentTitle(timer);
        builder.setContentText(time);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }


}
