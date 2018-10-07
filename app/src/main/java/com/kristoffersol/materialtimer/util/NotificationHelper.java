package com.kristoffersol.materialtimer.util;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.kristoffersol.materialtimer.NotificationReceiver;

public class NotificationHelper implements LifecycleObserver {

    private BroadcastReceiver notificationReciever;
    private IntentFilter notificationFilter;
    private LocalBroadcastManager localManager;

    public NotificationHelper(Context context){
        localManager = LocalBroadcastManager.getInstance(context);
        notificationReciever = new NotificationReceiver();
        notificationFilter = new IntentFilter();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void registerReceiver(){
        notificationFilter.addAction(Intent.ACTION_SCREEN_OFF);
        localManager.registerReceiver(notificationReciever,notificationFilter);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void unregisterReceiver(){
        localManager.unregisterReceiver(notificationReciever);
    }

}
