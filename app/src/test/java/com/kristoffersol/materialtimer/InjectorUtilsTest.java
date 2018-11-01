package com.kristoffersol.materialtimer;

import com.kristoffersol.materialtimer.data.PomodoroRepository;
import com.kristoffersol.materialtimer.util.InjectorUtils;
import com.kristoffersol.materialtimer.viewmodel.PomodoroViewModelFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.TestCase.assertEquals;


@RunWith( PowerMockRunner.class )
@PrepareForTest( InjectorUtils.class )
public class InjectorUtilsTest {

    @Mock
    private PomodoroViewModelFactory factory;

    @Mock
    private PomodoroRepository repository;

    @Before
    public void setUp(){
        PowerMockito.mockStatic(InjectorUtils.class);
    }

    @Test
    public void shouldReturnViewModelFactory(){
        //set expectations
        Mockito.when(InjectorUtils.providePomodoroViewModelFactory()).thenReturn(factory);
        assertEquals(factory, InjectorUtils.providePomodoroViewModelFactory());

        //verify behavior
        PowerMockito.verifyStatic(InjectorUtils.class);
        InjectorUtils.providePomodoroViewModelFactory();
    }

    @Test
    public void shouldReturnPomodoroRepository(){
        Mockito.when(InjectorUtils.providePomodoroRepository()).thenReturn(repository);
        assertEquals(repository, InjectorUtils.providePomodoroRepository());

        PowerMockito.verifyStatic(InjectorUtils.class);
        InjectorUtils.providePomodoroRepository();
    }
}
