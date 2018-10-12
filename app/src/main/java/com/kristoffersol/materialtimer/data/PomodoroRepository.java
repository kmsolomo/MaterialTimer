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

    public MutableLiveData<Boolean> getSessionStartedData(){
        return pomodoroDao.getSessionStartedData();
    }

    public void setSessionStartedData(Boolean state){
        pomodoroDao.setSessionStarted(state);
    }

    public Boolean getSessionStarted(){
        return pomodoroDao.getSessionStarted();
    }
}
