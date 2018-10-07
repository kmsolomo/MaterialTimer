package com.kristoffersol.materialtimer.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.kristoffersol.materialtimer.data.PomodoroRepository;

public class PomodoroViewModel extends ViewModel {

    private LiveData<String> currentTime;
    private MutableLiveData<Boolean> state;
    private MutableLiveData<Boolean> playPauseState;
    private MutableLiveData<Boolean> stopButtonState;
    private PomodoroRepository pomodoroRepository;

    private LiveData<String> timerModel;

    public PomodoroViewModel(PomodoroRepository pomodoroRepository){
        this.pomodoroRepository = pomodoroRepository;
        timerModel = this.pomodoroRepository.getTimeData();
        currentTime = Transformations.map(timerModel, time -> time);
    }

    public LiveData<String> getCurrentTime(){
        return currentTime;
    }

    public void setState(Boolean nextState){
        state.setValue(nextState);
    }

    public Boolean getState(){
        if(state == null){
            state = new MutableLiveData<>();
        }
        return state.getValue();
    }
}
