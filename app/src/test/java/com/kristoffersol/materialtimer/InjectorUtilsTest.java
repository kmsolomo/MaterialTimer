package com.kristoffersol.materialtimer;

import com.kristoffersol.materialtimer.data.PomodoroRepository;
import com.kristoffersol.materialtimer.util.InjectorUtils;
import com.kristoffersol.materialtimer.viewmodel.PomodoroViewModelFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;


@RunWith( PowerMockRunner.class )
@PrepareForTest( InjectorUtils.class )
public class InjectorUtilsTest {

    @Mock
    private PomodoroViewModelFactory factory;

    @Mock
    private PomodoroRepository repository;

    @Before
    public void setUp(){
        mockStatic(InjectorUtils.class);
    }

    @Test
    public void shouldReturnViewModelFactory(){
        //set expectations
        when(InjectorUtils.providePomodoroViewModelFactory()).thenReturn(factory);
        assertEquals(factory, InjectorUtils.providePomodoroViewModelFactory());

        //verify behavior
        verifyStatic(InjectorUtils.class);
        InjectorUtils.providePomodoroViewModelFactory();
    }

    @Test
    public void shouldReturnPomodoroRepository(){
        when(InjectorUtils.providePomodoroRepository()).thenReturn(repository);
        assertEquals(repository, InjectorUtils.providePomodoroRepository());

        verifyStatic(InjectorUtils.class);
        InjectorUtils.providePomodoroRepository();
    }
}
