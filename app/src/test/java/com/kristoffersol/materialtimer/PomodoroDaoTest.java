package com.kristoffersol.materialtimer;

import com.kristoffersol.materialtimer.data.PomodoroDao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import androidx.lifecycle.MutableLiveData;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PomodoroDaoTest {

    @Mock
    private PomodoroDao dao;

    @Mock
    private MutableLiveData<String> stringMutableLiveData;

    @Mock
    private MutableLiveData<Boolean> booleanMutableLiveData;

    @Test
    public void shouldReturnTimeData(){
        when(dao.getTimeData()).thenReturn(stringMutableLiveData);
        assertEquals(stringMutableLiveData, dao.getTimeData());
    }

    @Test
    public void shouldVerifySetTime(){
        doNothing().when(dao).setTime(anyString());
        dao.setTime(anyString());
        verify(dao,times(1)).setTime(anyString());
    }

    @Test
    public void shouldGetTime(){
        when(dao.getTime()).thenReturn("Test");
        assertEquals("Test", dao.getTime());
    }

    @Test
    public void shouldGetCurrState(){
        when(dao.getCurrState()).thenReturn(booleanMutableLiveData);
        assertEquals(booleanMutableLiveData, dao.getCurrState());
    }

    @Test
    public void shouldSetState(){
        doNothing().when(dao).setState(anyBoolean());
        dao.setState(anyBoolean());
        verify(dao,times(1)).setState(anyBoolean());
    }

    @Test
    public void shouldGetState(){
        when(dao.getState()).thenReturn(true);
        assertEquals(true, dao.getState());
    }

    @Test
    public void shouldGetSessionStartedData(){
        when(dao.getSessionStartedData()).thenReturn(booleanMutableLiveData);
        assertEquals(booleanMutableLiveData, dao.getSessionStartedData());
    }

    @Test
    public void shouldSetSessionStarted(){
        doNothing().when(dao).setSessionStarted(anyBoolean());
        dao.setSessionStarted(anyBoolean());
        verify(dao,times(1)).setSessionStarted(anyBoolean());
    }

    @Test
    public void shouldGetSessionStarted(){
        when(dao.getSessionStarted()).thenReturn(true);
        assertEquals(true, dao.getSessionStarted());
    }
}
