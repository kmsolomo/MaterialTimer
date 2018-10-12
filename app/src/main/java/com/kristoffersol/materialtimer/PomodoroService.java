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

package com.kristoffersol.materialtimer;

import android.app.Notification;
import androidx.lifecycle.LifecycleService;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.kristoffersol.materialtimer.data.PomodoroRepository;
import com.kristoffersol.materialtimer.model.PomodoroModel;
import com.kristoffersol.materialtimer.util.InjectorUtils;
import com.kristoffersol.materialtimer.util.NotificationUtil;

public class PomodoroService extends LifecycleService{

    public static final String SCREEN_OFF    = "timer_screen_off";
    public static final String ACTION_START  = "START";
    public static final String ACTION_PAUSE  = "PAUSE";
    public static final String ACTION_STOP   = "STOP";
    public static final String ACTION_RESET  = "RESET";

    private NotificationUtil notifUtil;
    private Vibrator vibrator;

    private PomodoroRepository pomodoroRepository;
    private PomodoroModel pomodoroModel;
    private Boolean connected, notification, session;

    @Override
    public void onCreate(){
        super.onCreate();
        connected = false;
        notification = false;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            vibrator = getSystemService(Vibrator.class);
        } else {
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        }

        notifUtil = new NotificationUtil(this);
        pomodoroModel = new PomodoroModel(this);
        pomodoroRepository = InjectorUtils.providePomodoroRepository();

        pomodoroModel.getTimer().observe(this, this::routeUpdate);
        pomodoroModel.getState().observe(this, this::updateState);
        pomodoroModel.getSessionState().observe(this, this::updateSession);
        pomodoroModel.refreshTimers();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        if(intent == null){
            pomodoroModel.restartTimer();
        } else {
            if(intent.getAction() != null){
                switch(intent.getAction()){
                    case ACTION_START:
                        pomodoroModel.startTimer();
                        handleNotification();
                        break;
                    case ACTION_PAUSE:
                        pomodoroModel.pauseTimer();
                        handleNotification();
                        break;
                    case ACTION_RESET:
                        pomodoroModel.resetTimer();
                        break;
                    case ACTION_STOP:
                        stopNotification();
                        stopSelf();
                        break;
                    case SCREEN_OFF:
                        screenOff();
                        break;
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent){
        super.onBind(intent);
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent){
        connected = false;
        if(session){
         startNotification();
        }
        return true;
    }

    @Override
    public void onRebind(Intent intent){
        super.onRebind(intent);
        connected = true;
        stopNotification();
    }

    private void routeUpdate(String time){
        if(notification){
            notifUtil.updateNotification(time,pomodoroModel.getCurrentTimer());
        }
        pomodoroRepository.setTime(time);
    }

    private void updateState(Boolean state){
        pomodoroRepository.setState(state);
    }

    private void updateSession(Boolean state){
        session = state;
    }

    private void handleNotification(){
        if(notification){
            startNotification();
        }
    }

    private void startNotification(){
        Notification pomNotification = notifUtil.buildNotification(pomodoroModel.getTime(), pomodoroModel.isRunning(), pomodoroModel.getCurrentTimer());
        startForeground(NotificationUtil.NOTIFICATION_ID, pomNotification);
        notification = true;
    }

    private void stopNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            stopForeground(STOP_FOREGROUND_REMOVE);
        } else {
            stopForeground(true);
        }
        notification = false;
    }

    /**
     * Start notification when sessions has started and user turns screen off
     */
    private void screenOff(){
        if(!connected && !notification && session){
            startNotification();
        }
    }

//    private void vibrate(){
//        boolean vibratePref = sharedPref.getBoolean(VIBRATION,false);
//        if(vibratePref){
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//                vibrator.vibrate(VibrationEffect.createOneShot(1000,VibrationEffect.DEFAULT_AMPLITUDE));
//            } else {
//                vibrator.vibrate(1000);
//            }
//        }
//    }

}