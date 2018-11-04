package com.kristoffersol.materialtimer;


import android.app.Notification;

import com.kristoffersol.materialtimer.util.NotificationUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationUtilTest {

    @Mock
    private NotificationUtil notificationUtil;

    @Mock
    private Notification notification;

    @Test
    public void shouldGetNotification(){
        when(notificationUtil.buildNotification(anyString(),
                                                anyBoolean(),
                                                anyString())).
                                                thenReturn(notification);

        assertEquals(notification,
                notificationUtil.buildNotification(anyString(),
                                                   anyBoolean(),
                                                   anyString()));

        verify(notificationUtil, times(1)).buildNotification(anyString(),
                                                                                   anyBoolean(),
                                                                                   anyString());
    }

    @Test
    public void shouldUpdateNotification(){
        doNothing().when(notificationUtil).updateNotification(anyString(),anyString());
        notificationUtil.updateNotification(anyString(),anyString());
        verify(notificationUtil,times(1)).updateNotification(anyString(),
                                                                                    anyString());
    }
}
