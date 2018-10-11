package com.kristoffersol.materialtimer.data;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

public class PomodoroDao {

    private MutableLiveData<String> currTime;
    private MutableLiveData<Boolean> currState;

    public PomodoroDao(){
        currTime = new MutableLiveData<>();
        currState = new MutableLiveData<>();
    }

    public MutableLiveData<String> getTimeData() {
        return currTime;
    }

    public void setTime(String time){
        currTime.setValue(time);
    }

    public String getTime(){
        return currTime.getValue();
    }

    public MutableLiveData<Boolean> getCurrState() {
        return currState;
    }

    public void setState(Boolean state){
        currState.setValue(state);
    }

    public Boolean getState(){
        return currState.getValue();
    }

}
