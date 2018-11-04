package com.kristoffersol.materialtimer;


import com.kristoffersol.materialtimer.viewmodel.PomodoroViewModel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import androidx.lifecycle.LiveData;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PomodoroViewModelTest {

    @Mock
    private PomodoroViewModel pomodoroViewModel;

    @Test
    public void getAnimationState(){
        when(pomodoroViewModel.getAnimationState()).thenReturn(true);
        assertEquals(true, pomodoroViewModel.getAnimationState());
        verify(pomodoroViewModel, times(1)).getAnimationState();
    }



}
