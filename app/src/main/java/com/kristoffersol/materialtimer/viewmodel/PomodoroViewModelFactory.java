package com.kristoffersol.materialtimer.viewmodel;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.kristoffersol.materialtimer.data.PomodoroRepository;

public class PomodoroViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private PomodoroRepository pomodoroRepository;

    public PomodoroViewModelFactory(PomodoroRepository pomodoroRepository){
        this.pomodoroRepository = pomodoroRepository;
    }

    @NonNull
    @SuppressLint("UNCHECKED_CAST")
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new PomodoroViewModel(pomodoroRepository);
    }
}
