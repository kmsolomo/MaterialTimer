package com.kristoffersol.materialtimer;


import android.content.Context;

import com.kristoffersol.materialtimer.model.PomodoroModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PomodoroModelTest {

    @Mock
    private Context mMockcontext;


    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

//    @Test
//    public void test1(){
//        PomodoroModel pomodoroModel = new PomodoroModel(mMockcontext);
//
//    }
}
