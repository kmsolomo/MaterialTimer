package com.kristoffersol.materialtimer.viewmodel;

import android.annotation.SuppressLint;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

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
