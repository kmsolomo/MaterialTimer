package com.kristoffersol.materialtimer;

import com.kristoffersol.materialtimer.data.AppDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AppDatabase.class)
public class AppDatabaseTest {

    @Mock
    private AppDatabase database;

    @Before
    public void setUp(){
        mockStatic(AppDatabase.class);
    }

    @Test
    public void shouldReturnDatabase(){
        when(AppDatabase.getInstance()).thenReturn(database);
        assertEquals(database, AppDatabase.getInstance());

        verifyStatic(AppDatabase.class);
        AppDatabase.getInstance();
    }
}
