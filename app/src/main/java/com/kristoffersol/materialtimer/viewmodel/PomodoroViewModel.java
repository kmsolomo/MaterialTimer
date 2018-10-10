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

package com.kristoffersol.materialtimer.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.kristoffersol.materialtimer.data.PomodoroRepository;

public class PomodoroViewModel extends ViewModel {

    private LiveData<String> currentTime;
    private LiveData<String> timerModel;
    private MutableLiveData<Boolean> stopButtonClickable,playPauseButtonClickable,stopButtonVisibility;
    private boolean animationState,timerRunning;
    private PomodoroRepository pomodoroRepository;


    public PomodoroViewModel(PomodoroRepository pomodoroRepository){
        this.pomodoroRepository = pomodoroRepository;
        timerModel = this.pomodoroRepository.getTimeData();
        currentTime = Transformations.map(timerModel, time -> time);
        stopButtonVisibility = new MutableLiveData<>();
        playPauseButtonClickable = new MutableLiveData<>();
        stopButtonClickable = new MutableLiveData<>();
        initDefaults();
    }

    private void initDefaults(){
        playPauseButtonClickable.setValue(true);
        stopButtonVisibility.setValue(false);
        stopButtonClickable.setValue(false);
        animationState = false;
        timerRunning = false;
    }

    public boolean getAnimationState(){
        return animationState;
    }

    public void setAnimationState(boolean state){
        animationState = state;
    }

    public boolean getTimerRunning(){
        return timerRunning;
    }

    public void setTimerRunning(boolean state){
        timerRunning = state;
    }

    public LiveData<Boolean> getPlayPauseButtonClickable(){
        return playPauseButtonClickable;
    }

    public void setPlayPauseButtonClickable(boolean state){
        playPauseButtonClickable.setValue(state);
    }

    public LiveData<Boolean> getStopButtonClickable(){
        return stopButtonClickable;
    }

    public void setStopButtonClickable(boolean state){
        stopButtonClickable.setValue(state);
    }

    public LiveData<Boolean> getStopButtonVisibility() {
        return stopButtonVisibility;
    }

    public void setStopButtonVisibility(boolean state){
        stopButtonVisibility.setValue(state);
    }

    public LiveData<String> getCurrentTime(){
        return currentTime;
    }

}
