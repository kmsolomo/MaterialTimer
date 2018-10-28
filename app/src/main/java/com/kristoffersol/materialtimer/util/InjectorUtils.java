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

package com.kristoffersol.materialtimer.util;


import com.kristoffersol.materialtimer.data.AppDatabase;
import com.kristoffersol.materialtimer.data.PomodoroRepository;
import com.kristoffersol.materialtimer.viewmodel.PomodoroViewModelFactory;

public final class InjectorUtils {

    private InjectorUtils(){}

    public static PomodoroViewModelFactory providePomodoroViewModelFactory(){
        PomodoroRepository pomodoroRepository = PomodoroRepository.getInstance(AppDatabase.getInstance().pomodoroDao);
        return new PomodoroViewModelFactory(pomodoroRepository);
    }

    public static PomodoroRepository providePomodoroRepository(){
        return PomodoroRepository.getInstance(AppDatabase.getInstance().pomodoroDao);
    }


}
