package com.kristoffersol.materialtimer;


import com.kristoffersol.materialtimer.util.NotificationHelper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class NotificationHelperTest {

    @Mock
    private NotificationHelper notificationHelper;

    @Test
    public void shouldRegisterReceiver(){
        doNothing().when(notificationHelper).registerReceiver();
        notificationHelper.registerReceiver();
        verify(notificationHelper,times(1)).registerReceiver();
    }

    @Test
    public void shouldUnregisterReceiver(){
        doNothing().when(notificationHelper).unregisterReceiver();
        notificationHelper.unregisterReceiver();
        verify(notificationHelper, times(1)).unregisterReceiver();
    }
}
