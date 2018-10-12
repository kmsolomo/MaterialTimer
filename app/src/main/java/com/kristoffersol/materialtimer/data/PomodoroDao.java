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

package com.kristoffersol.materialtimer.data;

import androidx.lifecycle.MutableLiveData;

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
