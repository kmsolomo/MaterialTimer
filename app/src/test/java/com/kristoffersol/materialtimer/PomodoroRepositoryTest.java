package com.kristoffersol.materialtimer;


import com.kristoffersol.materialtimer.data.PomodoroDao;
import com.kristoffersol.materialtimer.data.PomodoroRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import androidx.lifecycle.MutableLiveData;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PomodoroRepository.class)
public class PomodoroRepositoryTest {

    @Mock
    private PomodoroDao dao;
    @Mock
    private PomodoroRepository repository;
    @Mock
    private MutableLiveData<String> stringMutableLiveData;
    @Mock
    private MutableLiveData<Boolean> booleanMutableLiveData;

    @Before
    public void setUp(){ mockStatic(PomodoroRepository.class); }

    @Test
    public void shouldGetInstance(){
        when(PomodoroRepository.getInstance(dao)).thenReturn(repository);
        assertEquals(repository,PomodoroRepository.getInstance(dao));

        verifyStatic(PomodoroRepository.class);
        PomodoroRepository.getInstance(dao);
    }

    @Test
    public void shouldGetTimeData(){
        when(repository.getTimeData()).thenReturn(stringMutableLiveData);
        assertEquals(stringMutableLiveData, repository.getTimeData());
        verify(repository).getTimeData();
    }

    @Test
    public void shouldGetCurrentTime(){
        when(repository.getCurrentTime()).thenReturn("Test");
        assertEquals("Test",repository.getCurrentTime());
        verify(repository).getCurrentTime();
    }

    @Test
    public void shouldSetTime(){
        doNothing().when(repository).setTime(anyString());
        repository.setTime(anyString());
        verify(repository,times(1)).setTime(anyString());
    }

    @Test
    public void shouldGetStateData(){
        when(repository.getStateData()).thenReturn(booleanMutableLiveData);
        assertEquals(booleanMutableLiveData, repository.getStateData());
        verify(repository).getStateData();
    }

    @Test
    public void shouldGetState(){
        when(repository.getState()).thenReturn(true);
        assertEquals(true, repository.getState());
        verify(repository).getState();
    }

    @Test
    public void shouldSetState(){
        doNothing().when(repository).setState(anyBoolean());
        repository.setState(anyBoolean());
        verify(repository, times(1)).setState(anyBoolean());
    }

    @Test
    public void shouldGetSessionStartedData(){
        when(repository.getSessionStartedData()).thenReturn(booleanMutableLiveData);
        assertEquals(booleanMutableLiveData,repository.getSessionStartedData());
        verify(repository,times(1)).getSessionStartedData();
    }

    @Test
    public void shouldSetSessionStartedData(){
        doNothing().when(repository).setSessionStartedData(anyBoolean());
        repository.setSessionStartedData(anyBoolean());
        verify(repository,times(1)).setSessionStartedData(anyBoolean());
    }

    @Test
    public void shouldGetSessionStarted(){
        when(repository.getSessionStarted()).thenReturn(true);
        assertEquals(true, repository.getSessionStarted());
        verify(repository,times(1)).getSessionStarted();
    }
}
