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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class TimerReceiver extends BroadcastReceiver {

    private void startService(Context context, Intent intent){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //context.startForegroundService(intent);
            context.startService(intent);
        } else {
            context.startService(intent);
        }
    }

    /**
     * Interface with PomodoroService
     */

    @Override
    public void onReceive(Context context, Intent intent){
        if(intent.getAction() != null){
            switch(intent.getAction()){
                case PomodoroService.ACTION_PAUSE:
                    Intent pauseIntent = new Intent(context, PomodoroService.class);
                    pauseIntent.setAction(PomodoroService.ACTION_PAUSE);
                    startService(context,pauseIntent);
                    break;
                case PomodoroService.ACTION_START:
                    Intent startIntent = new Intent(context, PomodoroService.class);
                    startIntent.setAction(PomodoroService.ACTION_START);
                    startService(context,startIntent);
                    break;
                case PomodoroService.ACTION_RESET:
                    Intent resetIntent = new Intent(context, PomodoroService.class);
                    resetIntent.setAction(PomodoroService.ACTION_RESET);
                    startService(context,resetIntent);
                    break;
                case PomodoroService.ACTION_STOP:
                    Intent stopIntent = new Intent(context, PomodoroService.class);
                    stopIntent.setAction(PomodoroService.ACTION_STOP);
                    startService(context,stopIntent);
                    break;
            }
        }
    }
}
