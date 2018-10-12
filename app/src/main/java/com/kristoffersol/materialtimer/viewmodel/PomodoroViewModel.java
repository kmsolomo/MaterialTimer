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

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.kristoffersol.materialtimer.data.PomodoroRepository;

public class PomodoroViewModel extends ViewModel {

    private LiveData<String> currentTime;
    private LiveData<Boolean> timerRunning;
    private LiveData<Boolean> sessionStarted;
    private MutableLiveData<Boolean> stopButtonClickable,playPauseButtonClickable,stopButtonVisibility;
    private boolean animationState;


    public PomodoroViewModel(PomodoroRepository pomodoroRepository){
        currentTime = Transformations.map(pomodoroRepository.getTimeData(), time -> time);
        timerRunning = Transformations.map(pomodoroRepository.getStateData(), state -> state);
        sessionStarted = Transformations.map(pomodoroRepository.getSessionStartedData(), state -> {
            animationState = state;
            return state;
        });
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
    }

    public boolean getAnimationState(){
        return animationState;
    }

    public void setAnimationState(boolean state){
        animationState = state;
    }

    public Boolean getTimerRunning() {
        if(timerRunning.getValue() != null){
            return timerRunning.getValue();
        }
        return false;
    }

    public LiveData<Boolean> getSessionData(){
        return sessionStarted;
    }

    public LiveData<Boolean> getStateData(){
        return timerRunning;
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
