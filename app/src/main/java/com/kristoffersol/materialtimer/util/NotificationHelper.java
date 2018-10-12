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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


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
