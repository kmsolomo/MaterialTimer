package com.kristoffersol.materialtimer.model;

public class PomodoroModel {

    public enum PomodoroState{
        STOPPED,
        RUNNING,
        PAUSED
    }

    private String currentTime;
    private Boolean state;

    public PomodoroModel(String time, Boolean state){
        currentTime = time;
        this.state = state;
    }

    public String getTime(){
        return currentTime;
    }
}
