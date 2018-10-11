package com.kristoffersol.materialtimer.data;

import android.arch.lifecycle.MutableLiveData;

public class PomodoroRepository {

    private static volatile PomodoroRepository instance = null;
    private PomodoroDao pomodoroDao;

    private PomodoroRepository(PomodoroDao dao) { pomodoroDao = dao;}

    public static PomodoroRepository getInstance(PomodoroDao pomodoroDao){
        if(instance == null){
            synchronized (PomodoroRepository.class){
                if(instance == null){
                 instance = new PomodoroRepository(pomodoroDao);
                }
            }
        }
        return instance;
    }

    public MutableLiveData<String> getTimeData(){
        return pomodoroDao.getTimeData();
    }

    public String getCurrentTime(){
        return pomodoroDao.getTime();
    }

    public void setTime(String time){
        pomodoroDao.setTime(time);
    }

    public MutableLiveData<Boolean> getStateData() { return pomodoroDao.getCurrState(); }

    public Boolean getState(){
        return pomodoroDao.getState();
    }

    public void setState(Boolean state){
        pomodoroDao.setState(state);
    }
}
