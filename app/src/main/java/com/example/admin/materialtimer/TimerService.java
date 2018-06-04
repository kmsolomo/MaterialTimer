package com.example.admin.materialtimer;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;


/**
 * Created by admin on 4/21/18.
 */

public class TimerService extends Service{

    public enum Timer{
        Work, Break, LongBreak
    }

    private Timer currentTimer;
    private HandlerThread workerThread;
    private long milliSecondsLeft,countDownInterval,workTime,breakTime,longBreakTime;
    private int sessionBeforeLongBreak,sessionCount;
    private boolean sessionStart,customFlag,connected,notification,running;
    private SharedPreferences sharedPref;
    private CountDownTimer timer;
    private NotificationUtil notifUtil;
    private Messenger timerMessenger, uiMessenger;
    private Vibrator vibrator;

    private final String WORK_TIME = "pref_work_time";
    private final String BREAK_TIME = "pref_break_time";
    private final String LONG_BREAK_TIME = "pref_long_break_time";
    private final String LOOP_AMOUT_VALUE = "pref_loop_amount";
    private final String VIBRATION = "pref_vibrate";

    public static final String TIMER_REFRESH = "timer_service_refresh";
    public static final String SCREEN_OFF = "timer_screen_off";
    public static final String ACTION_START = "start";
    public static final String ACTION_PAUSE = "pause";
    public static final String ACTION_RESET = "reset";
    public static final int START_TIMER = 1;
    public static final int PAUSE_TIMER = 2;
    public static final int RESET_TIMER = 3;
    public static final int REGISTER_CLIENT = 4;
    public static final int START_NOTIFICATION = 5;
    public static final int STOP_NOTIFICATION = 6;

    /**
     * Handler for incoming messages from clients.
     */
    private class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper){
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_TIMER:
                    startTimer();
                    break;
                case PAUSE_TIMER:
                    pauseTimer();
                    break;
                case RESET_TIMER:
                    resetTimer();
                    break;
                case REGISTER_CLIENT:
                    uiMessenger = msg.replyTo;
                    synchronizeClient();
                    break;
                case START_NOTIFICATION:
                    startNotification();
                    break;
                case STOP_NOTIFICATION:
                    stopNotification();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
        currentTimer = Timer.Work;
        sessionStart = false;
        customFlag = false;
        connected = false;
        notification = false;
        running = false;
        sessionCount = 0;
        countDownInterval = 300;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            vibrator = getSystemService(Vibrator.class);
        } else {
            vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        }

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        notifUtil = new NotificationUtil(this);
        workerThread = new HandlerThread("WorkerThread", Process.THREAD_PRIORITY_DEFAULT);
        workerThread.start();
        timerMessenger = new Messenger(new ServiceHandler(workerThread.getLooper()));
        refreshTimers();
        Log.v("TimerService","onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if(intent == null){
            restartTimer();
        } else {
            if(intent.getAction() != null){
                switch(intent.getAction()){
                    case ACTION_START:
                        startAction();
                        break;
                    case ACTION_PAUSE:
                        pauseAction();
                        break;
                    case ACTION_RESET:
                        stopTimer();
                        break;
                    case SCREEN_OFF:
                        screenOff();
                        break;
                    default:
                        break;
                }
            }
        }
        return START_STICKY;
    }

    private void pauseAction(){
        pauseTimer();
        startNotification();
        saveTimerState();
    }

    private void startAction(){
        startTimer();
        startNotification();
        saveTimerState();
    }

    private void restartTimer(){
        restoreTimerState();
        if(running) {
            running = false;
            startTimer();
        }
        startNotification();
        saveTimerState();
    }

    @Override
    public IBinder onBind(Intent intent){
        return timerMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent){
        uiMessenger = null;
        connected = false;
        return true;
    }

    @Override
    public void onRebind(Intent intent){
        super.onRebind(intent);
        connected = true;
    }

    @Override
    public void onDestroy(){
        saveTimerState();
        if(workerThread != null && workerThread.isAlive()){
            workerThread.quit();
        }
        super.onDestroy();
        Log.v("TimerService","onDestroy()");
    }

    private void restoreTimerState(){
        sessionStart = sharedPref.getBoolean("sessionStart",false);
        customFlag = sharedPref.getBoolean("customFlag",false);
        connected = sharedPref.getBoolean("connected",false);
        notification = sharedPref.getBoolean("notification",true);
        running = sharedPref.getBoolean("running",false);
        sessionCount = sharedPref.getInt("sessionCount",0);
        sessionBeforeLongBreak = sharedPref.getInt("sessionBeforeLongBreak",4);
        milliSecondsLeft = getTime();

        int timerState = sharedPref.getInt("currentTimer",0);
        if(timerState == 0){
            currentTimer = Timer.Work;
        } else if(timerState == 1){
            currentTimer = Timer.Break;
        } else{
            currentTimer = Timer.LongBreak;
        }

        if(running){
            startForeground(NotificationUtil.NOTIFICATION_ID,
                    notifUtil.buildNotification(formatTime(getTime()),
                            true,getTimer()));
            notifUtil.updateNotification(formatTime(getTime()),getTimer());
        } else {
            startForeground(NotificationUtil.NOTIFICATION_ID,
                    notifUtil.buildNotification(formatTime(getTime()),
                            false,getTimer()));
            notifUtil.updateNotification(formatTime(getTime()),getTimer());
        }
        Log.v("TimerService","restoreTimerState");
    }

    private void saveTimerState(){
        saveTime();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("sessionStart",sessionStart);
        editor.putBoolean("customFlag",customFlag);
        editor.putBoolean("connected",connected);
        editor.putBoolean("notification",notification);
        editor.putBoolean("running",running);
        editor.putInt("sessionCount",sessionCount);
        editor.putInt("sessionBeforeLongBreak",sessionBeforeLongBreak);

        if(currentTimer == Timer.Work){
            editor.putInt("currentTimer",0);
        } else if(currentTimer == Timer.Break){
            editor.putInt("currentTimer",1);
        } else {
            editor.putInt("currentTimer",2);
        }

        editor.apply();
    }

    private void synchronizeClient(){
        connected = true;
        Message msgState = Message.obtain();
        msgState.what = TimerActivity.UPDATE_STATE;
        msgState.obj = running;

        if(currentTimer == Timer.Work && !sessionStart){
            refreshTimers();
            updateTimer(convertTime(sharedPref.getInt(WORK_TIME,25)));
            msgState.arg1 = 0;
            Log.v("TimerService","if syncClient()");
        } else {
            if(notification){
                stopNotification();
            }
            updateTimer(milliSecondsLeft);
            msgState.arg1 = 1;
        }

        try{
            uiMessenger.send(msgState);
        } catch (RemoteException e){
            Log.d("TimerService", e.toString());
        }
        Log.v("TimerService","synchronizeClient");
    }

    private void startNotification(){
        if(running){
            startForeground(NotificationUtil.NOTIFICATION_ID, notifUtil.buildNotification(formatTime(getTime()),true,getTimer()));
        } else {
            startForeground(NotificationUtil.NOTIFICATION_ID, notifUtil.buildNotification(formatTime(getTime()),false,getTimer()));
        }
        notifUtil.updateNotification(formatTime(getTime()),getTimer());
        notification = true;
        Log.v("startNotification","startNotification");
    }

    private void stopNotification(){
        if(notification){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                stopForeground(STOP_FOREGROUND_REMOVE);
            } else {
                stopForeground(true);
            }
            notification = false;
        }
        Log.v("stopNotification","stopNotification");
    }

    private void screenOff(){
        if(!connected && sessionStart && !notification){
            startNotification();
        }
    }

    private void saveTime(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong("timeLeft",milliSecondsLeft);
        editor.apply();
    }

    private long getTime(){
        return sharedPref.getLong("timeLeft",0);
    }

    private long convertTime(int value){
        return Long.valueOf(value) * 60000;
    }

    private String getTimer(){
        if(currentTimer == Timer.Work){
            return "Work";
        } else if (currentTimer == Timer.Break){
            return "Break";
        } else {
            return "Long Break";
        }
    }

    private String formatTime(long milliSecondsLeft){
        int minutes = (int) milliSecondsLeft / 60000;
        int seconds = (int) milliSecondsLeft % 60000 / 1000;
        String currentTime = "";

        if (minutes < 10) {
            currentTime = "0" + minutes;
        } else {
            currentTime += minutes;
        }

        currentTime += ":";

        if (seconds < 10) {
            currentTime += "0" + seconds;
        } else {
            currentTime += seconds;
        }
        return currentTime;
    }

    private void vibrate(){
        boolean vibratePref = sharedPref.getBoolean(VIBRATION,false);
        if(vibratePref){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                vibrator.vibrate(VibrationEffect.createOneShot(1000,VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(1000);
            }
        }
    }

    private void refreshTimers(){
        workTime = convertTime(sharedPref.getInt(WORK_TIME,25));
        breakTime = convertTime(sharedPref.getInt(BREAK_TIME,5));
        longBreakTime = convertTime(sharedPref.getInt(LONG_BREAK_TIME,15));
        sessionBeforeLongBreak = sharedPref.getInt(LOOP_AMOUT_VALUE,4);
    }

    private void startTimer(){
        if(currentTimer == Timer.Work && !sessionStart && !running){
            sessionStart = true;
            running = true;
            timer(convertTime(sharedPref.getInt(WORK_TIME,25)));
        } else if(!running){
            running = true;
            timer(milliSecondsLeft);
        }
    }

    private void pauseTimer(){
        if(running){
            running = false;
            saveTime();
            timer.cancel();
        }
    }

    private void stopTimer(){
        stopNotification();
        stopSelf();
    }

    private void resetTimer(){
        pauseTimer();
        currentTimer= Timer.Work;
        sessionStart = false;
        refreshTimers();
        synchronizeClient();
    }

    private void timer(long timeLeft){
        timer = new CountDownTimer(timeLeft,countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                milliSecondsLeft = millisUntilFinished;
                updateTimer(milliSecondsLeft);
            }

            @Override
            public void onFinish() {
                vibrate();
                customFlag = false;
                switch (currentTimer){
                    case Work:
                        if(sessionCount < sessionBeforeLongBreak){
                            sessionCount++;
                            currentTimer = Timer.Break;
                            refreshTimers();
                            timer(breakTime);
                        } else {
                            sessionCount = 0;
                            currentTimer = Timer.LongBreak;
                            timer(longBreakTime);
                        }
                        break;
                    case Break:
                        currentTimer = Timer.Work;
                        refreshTimers();
                        timer(workTime);
                        break;
                    case LongBreak:
                        currentTimer = Timer.Work;
                        refreshTimers();
                        timer(workTime);
                        break;
                    default:
                        break;
                }
            }
        }.start();
    }

    private void updateTimer(long milliSecondsLeft){
        String currentTime = formatTime(milliSecondsLeft);
        String currentTimer = getTimer();
        if(notification){
            notifUtil.updateNotification(currentTime,currentTimer);
        } else {
            if(uiMessenger != null){
                Message updateUI = Message.obtain();
                updateUI.what = TimerActivity.UPDATE_TIME;
                updateUI.obj = currentTime;
                try{
                    uiMessenger.send(updateUI);
                } catch(RemoteException e){
                    Log.e("RemoteException",e.toString());
                }
            }
        }
        saveTimerState();
    }
}