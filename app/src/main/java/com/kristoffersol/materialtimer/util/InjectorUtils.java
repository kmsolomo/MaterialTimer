package com.kristoffersol.materialtimer.util;


import com.kristoffersol.materialtimer.data.AppDatabase;
import com.kristoffersol.materialtimer.data.PomodoroRepository;
import com.kristoffersol.materialtimer.viewmodel.PomodoroViewModelFactory;

public class InjectorUtils {

    public static PomodoroViewModelFactory providePomodoroViewModelFactory(){
        PomodoroRepository pomodoroRepository = PomodoroRepository.getInstance(AppDatabase.getInstance().pomodoroDao);
        return new PomodoroViewModelFactory(pomodoroRepository);
    }

    public static PomodoroRepository providePomodoroRepository(){
        return PomodoroRepository.getInstance(AppDatabase.getInstance().pomodoroDao);
    }


}
