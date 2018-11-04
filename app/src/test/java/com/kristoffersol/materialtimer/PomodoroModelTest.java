package com.kristoffersol.materialtimer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import androidx.lifecycle.LiveData;
import com.kristoffersol.materialtimer.model.PomodoroModel;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PomodoroModelTest {

    @Mock
    private PomodoroModel pomodoroModel;

    @Mock
    private LiveData<String> stringLiveData;

    @Mock
    private LiveData<Boolean> booleanLiveData;

    @Test
    public void shouldGetTimer(){
        when(pomodoroModel.getTimer()).thenReturn(stringLiveData);
        assertEquals(stringLiveData, pomodoroModel.getTimer());
        verify(pomodoroModel,times(1)).getTimer();
    }

    @Test
    public void shouldGetTime(){
        when(pomodoroModel.getTime()).thenReturn("TIME");
        assertEquals("TIME",pomodoroModel.getTime());
        verify(pomodoroModel,times(1)).getTime();
    }

    @Test
    public void shouldGetState(){
        when(pomodoroModel.getState()).thenReturn(booleanLiveData);
        assertEquals(booleanLiveData, pomodoroModel.getState());
        verify(pomodoroModel, times(1)).getState();
    }

    @Test
    public void isRunning(){
        when(pomodoroModel.isRunning()).thenReturn(true);
        assertEquals(true, pomodoroModel.isRunning());
        verify(pomodoroModel, times(1)).isRunning();
    }

    @Test
    public void getSessionState(){
        when(pomodoroModel.getSessionState()).thenReturn(booleanLiveData);
        assertEquals(booleanLiveData, pomodoroModel.getSessionState());
        verify(pomodoroModel, times(1)).getSessionState();
    }

    @Test
    public void shouldRefreshTimers(){
        doNothing().when(pomodoroModel).refreshTimers();
        pomodoroModel.refreshTimers();
        verify(pomodoroModel,times(1)).refreshTimers();
    }

    @Test
    public void shouldStartTimer(){
        doNothing().when(pomodoroModel).startTimer();
        pomodoroModel.startTimer();
        verify(pomodoroModel, times(1)).startTimer();
    }

    @Test
    public void shouldPauseTimer(){
        doNothing().when(pomodoroModel).pauseTimer();
        pomodoroModel.pauseTimer();
        verify(pomodoroModel, times(1)).pauseTimer();
    }

    @Test
    public void shouldRestartTimer(){
        doNothing().when(pomodoroModel).restartTimer();
        pomodoroModel.restartTimer();
        verify(pomodoroModel, times(1)).restartTimer();
    }

    @Test
    public void shouldResetTimer(){
        doNothing().when(pomodoroModel).resetTimer();
        pomodoroModel.resetTimer();
        verify(pomodoroModel, times(1)).resetTimer();
    }

    @Test
    public void getCurrentTimer(){
        when(pomodoroModel.getCurrentTimer()).thenReturn("TEST");
        assertEquals("TEST", pomodoroModel.getCurrentTimer());
        verify(pomodoroModel,times(1)).getCurrentTimer();
    }
}
