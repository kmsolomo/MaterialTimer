package com.kristoffersol.materialtimer;

import android.content.Context;
import android.content.res.Resources;

import com.kristoffersol.materialtimer.util.ThemeUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith( PowerMockRunner.class )
@PrepareForTest( ThemeUtil.class )
public class ThemeUtilTest {

    @Mock
    private Context context;

    @Test
    public void shouldSetTheme(){
        mockStatic(ThemeUtil.class);

        ThemeUtil partialThemeUtil = spy(new ThemeUtil());
        partialThemeUtil.themeCheck(context);

        verifyStatic(ThemeUtil.class);
        ThemeUtil.themeCheck(context);

        verifyNoMoreInteractions(ThemeUtil.class);
    }

}
