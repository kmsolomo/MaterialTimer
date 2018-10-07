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

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.preference.PreferenceManager;
import android.os.Bundle;

import com.kristoffersol.materialtimer.databinding.ActivityMainBinding;

public class TimerActivity extends BaseActivity {

    ActivityMainBinding mBinding;

//    /**
//     * Communicate with PomodoroService to launch notification
//     */
//
//    private void startNotification(){
//        if(timerStatus != TimerState.Stopped){
//            Message notifyMsg = Message.obtain();
//            notifyMsg.what = PomodoroService.START_NOTIFICATION;
//            try{
//                timerMessenger.send(notifyMsg);
//            } catch (RemoteException e){
//                Log.e("RemoteException",e.toString());
//            }
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setupToolbar();

        PreferenceManager.setDefaultValues(this,R.xml.preferences,false);

        if(savedInstanceState == null){
            addFragmentToActivity(getSupportFragmentManager(), R.id.fragment_container, new PomodoroFragment());
        }

        //insures service persists bound lifecycle
        Intent timerIntent = new Intent(TimerActivity.this, PomodoroService.class);
        startService(timerIntent);

    }

    private void setupToolbar(){
        mBinding.SettingsButton.setOnClickListener( view -> {
            Intent data = new Intent(TimerActivity.this,SettingsActivity.class);
            startActivityForResult(data,THEME_REQUEST_CODE);
        });
    }

    @Override
    public void onUserLeaveHint(){
//        startNotification();
    }


    //possibly not needed if onUserLeaveHint is implemented
    @Override
    public void onBackPressed(){
//        startNotification();
        super.onBackPressed();
    }
}