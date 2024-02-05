package org.rponte.appmockito.ejemplos.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.rponte.appmockito.ejemplos.Datos;
import org.rponte.appmockito.ejemplos.models.Examen;
import org.rponte.appmockito.ejemplos.repositories.ExamenRepository;
import org.rponte.appmockito.ejemplos.repositories.ExamenRepositoryImpl;
import org.rponte.appmockito.ejemplos.repositories.PreguntaRepository;
import org.rponte.appmockito.ejemplos.repositories.PreguntaRepositoryImpl;
import org.rponte.appmockito.ejemplos.services.impl.ExamenServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {
    @Mock
    ExamenRepositoryImpl examenRepository;
    @Mock
    PreguntaRepositoryImpl preguntaRepository;
    @InjectMocks
    ExamenServiceImpl examenService;

    @Captor
    ArgumentCaptor<Long> captor;

    @BeforeEach
    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        examenRepository = mock(ExamenRepositoryImpl.class);
//        preguntaRepository = mock(PreguntaRepositoryImpl.class);
//        examenService = new ExamenServiceImpl(examenRepository, preguntaRepository);
    }

    @Test
    void findExamenPorNombre() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        Optional<Examen> examen = examenService.findExamenPorNombre("Matemáticas");

        assertTrue(examen.isPresent());
        assertEquals(5L, examen.orElseThrow().getId());
        assertEquals("Matemáticas", examen.get().getNombre());
    }

    @Test
    void findExamenPorNombreListaVacia() {
        List<Examen> datos = Collections.emptyList();

        when(examenRepository.findAll()).thenReturn(datos);
        Optional<Examen> examen = examenService.findExamenPorNombre("Matemáticas");

        assertFalse(examen.isPresent());
    }

    @Test
    void testPreguntasExamen() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamen(anyLong())).thenReturn(Datos.PREGUNTAS);

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matemáticas");

        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmética"));
    }

    @Test
    void testPreguntasExamenVerify() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamen(anyLong())).thenReturn(Datos.PREGUNTAS);

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matemáticas");

        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmética"));
        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamen(5L);
    }

    @Test
    void testNoExisteExamenVerify() {
        // Given
        when(examenRepository.findAll()).thenReturn(Collections.emptyList());
        when(preguntaRepository.findPreguntasPorExamen(anyLong())).thenReturn(Datos.PREGUNTAS);

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matemáticas");

        assertNull(examen);
        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamen(anyLong());
    }

    @Test
    void guardarExamen() {
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);

        when(examenRepository.guardar(any(Examen.class))).thenReturn(Datos.EXAMEN);
        Examen examen = examenService.guardar(Datos.EXAMEN);

        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Física", examen.getNombre());

        verify(examenRepository).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());
    }

    @Test
    void testGuardarExamen() {
        // Given
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);

        when(examenRepository.guardar(any(Examen.class))).then(new Answer<Examen>() {
            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen = invocationOnMock.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        });

        // When
        Examen examen = examenService.guardar(newExamen);

        // Then
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Física", examen.getNombre());

        verify(examenRepository).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());
    }

    @Test
    void testManejoException() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamen(anyLong())).thenThrow(IllegalArgumentException.class);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            examenService.findExamenPorNombreConPreguntas("Matemáticas");
        });

        assertEquals(IllegalArgumentException.class, exception.getClass());

        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamen(anyLong());
    }

    @Test
    void testManejoExceptionNull() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES_ID_NULL);
        when(preguntaRepository.findPreguntasPorExamen(isNull())).thenThrow(IllegalArgumentException.class);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            examenService.findExamenPorNombreConPreguntas("Matemáticas");
        });

        assertEquals(IllegalArgumentException.class, exception.getClass());

        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamen(isNull());
    }

    @Test
    void testArgumentMatchers() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamen(anyLong())).thenReturn(Datos.PREGUNTAS);

        examenService.findExamenPorNombreConPreguntas("Matemáticas");

        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamen(argThat(arg -> arg != null && arg.equals(5L)));
        verify(preguntaRepository).findPreguntasPorExamen(argThat(arg -> arg != null && arg >= 5L));
        verify(preguntaRepository).findPreguntasPorExamen(eq(5L));
    }

    @Test
    void testArgumentMatchers2() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES_ID_NEGATIVOS);
        when(preguntaRepository.findPreguntasPorExamen(anyLong())).thenReturn(Datos.PREGUNTAS);

        examenService.findExamenPorNombreConPreguntas("Matemáticas");

        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamen(argThat(new MiArgsMatchers()));
    }

    @Test
    void testArgumentMatchers3() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES_ID_NEGATIVOS);
        when(preguntaRepository.findPreguntasPorExamen(anyLong())).thenReturn(Datos.PREGUNTAS);

        examenService.findExamenPorNombreConPreguntas("Matemáticas");

        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamen(
                argThat((argument) -> argument != null && argument > 0));
    }

    public static class MiArgsMatchers implements ArgumentMatcher<Long> {
        private Long argument;

        @Override
        public boolean matches(Long argument) {
            this.argument = argument;
            return argument != null && argument > 0;
        }

        @Override
        public String toString() {
            return "es para un mensaje personalizado de error que imprime mockito en caso " +
                    "de que falle el test" + argument + " debe ser un entero positivo";
        }
    }

    @Test
    void testArgumentCaptor() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        //when(preguntaRepository.findPreguntasPorExamen(anyLong())).thenReturn(Datos.PREGUNTAS);
        examenService.findExamenPorNombreConPreguntas("Matemáticas");

        //ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        verify(preguntaRepository).findPreguntasPorExamen(captor.capture());
        assertEquals(5L, captor.getValue());
    }

    @Test
    void testDoThrow() {
        Examen examen = Datos.EXAMEN;
        examen.setPreguntas(Datos.PREGUNTAS);
        doThrow(IllegalArgumentException.class).when(preguntaRepository).guardarVarias(anyList());

        assertThrows(IllegalArgumentException.class, () -> {
            examenService.guardar(examen);
        });
    }

    @Test
    void testDoAnswer() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
//        when(preguntaRepository.findPreguntasPorExamen(anyLong())).thenReturn(Datos.PREGUNTAS);

        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return id == 5L ? Datos.PREGUNTAS : Collections.emptyList();
        }).when(preguntaRepository).findPreguntasPorExamen(anyLong());

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matemáticas");

        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("geometría"));
        assertEquals(5L, examen.getId());
        assertEquals("Matemáticas", examen.getNombre());

        verify(preguntaRepository).findPreguntasPorExamen(anyLong());
    }

    @Test
    void testDoAnswerGuardarExamen() {
        // Given
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);

//        when(examenRepository.guardar(any(Examen.class))).then(new Answer<Examen>() {
//            Long secuencia = 8L;
//
//            @Override
//            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
//                Examen examen = invocationOnMock.getArgument(0);
//                examen.setId(secuencia++);
//                return examen;
//            }
//        });

        doAnswer(new Answer<Examen>() {
            Long secuencia = 8L;

            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen = invocationOnMock.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        }).when(examenRepository).guardar(any(Examen.class));

        // When
        Examen examen = examenService.guardar(newExamen);

        // Then
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Física", examen.getNombre());

        verify(examenRepository).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());
    }

    @Test
    void testDoCallRealMethod() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        //when(preguntaRepository.findPreguntasPorExamen(anyLong())).thenReturn(Datos.PREGUNTAS);

        doCallRealMethod().when(preguntaRepository).findPreguntasPorExamen(anyLong());

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matemáticas");

        assertEquals(5L, examen.getId());
        assertEquals("Matemáticas", examen.getNombre());
    }

    @Test
    void testSpy() {
        // Mock simula los métodos, Spy requiere que se cree a partir de una clase concreta
        // no desde una clase abstracta
        ExamenRepository examenRepository = spy(ExamenRepositoryImpl.class);
        PreguntaRepository preguntaRepository = spy(PreguntaRepositoryImpl.class);
        ExamenService examenService = new ExamenServiceImpl(examenRepository, preguntaRepository);

        List<String> preguntas = Arrays.asList("aritmética");
//        when(preguntaRepository.findPreguntasPorExamen(anyLong())).thenReturn(preguntas);
        doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamen(anyLong());

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5, examen.getId());
        assertEquals("Matemáticas", examen.getNombre());
        assertEquals(1, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmética"));
        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamen(anyLong());
    }

    @Test
    void testOrdenDeInvocaciones() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);

        examenService.findExamenPorNombreConPreguntas("Matemáticas");
        examenService.findExamenPorNombreConPreguntas("Lenguaje");

        InOrder inOrder = inOrder(examenRepository, preguntaRepository);
        inOrder.verify(examenRepository).findAll();
        inOrder.verify(preguntaRepository).findPreguntasPorExamen(5L);
        inOrder.verify(examenRepository).findAll();
        inOrder.verify(preguntaRepository).findPreguntasPorExamen(6L);
    }

    @Test
    void testNumeroDeInvocaciones() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        examenService.findExamenPorNombreConPreguntas("Matemáticas");

        verify(preguntaRepository).findPreguntasPorExamen(5L);
        verify(preguntaRepository, times(1)).findPreguntasPorExamen(5L);
        verify(preguntaRepository, atLeast(1)).findPreguntasPorExamen(5L);
        verify(preguntaRepository, atLeastOnce()).findPreguntasPorExamen(5L);
        verify(preguntaRepository, atMost(1)).findPreguntasPorExamen(5L);
        verify(preguntaRepository, atMostOnce()).findPreguntasPorExamen(5L);
    }

    @Test
    void testNumeroDeInvocaciones2() {
        when(examenRepository.findAll()).thenReturn(Datos.EXAMENES);
        examenService.findExamenPorNombreConPreguntas("Matemáticas");

        verify(preguntaRepository).findPreguntasPorExamen(5L);
        verify(preguntaRepository, times(2)).findPreguntasPorExamen(5L);
        verify(preguntaRepository, atLeast(2)).findPreguntasPorExamen(5L);
        verify(preguntaRepository, atLeastOnce()).findPreguntasPorExamen(5L);
        verify(preguntaRepository, atMost(2)).findPreguntasPorExamen(5L);
        //verify(preguntaRepository, atMostOnce()).findPreguntasPorExamen(5L);
    }

    @Test
    void testNumeroInvocaciones3() {
        when(examenRepository.findAll()).thenReturn(Collections.emptyList());
        examenService.findExamenPorNombreConPreguntas("Matemáticas");

        verify(preguntaRepository, never()).findPreguntasPorExamen(5L);
        verifyNoInteractions(preguntaRepository);

        verify(examenRepository).findAll();
        verify(examenRepository, times(1)).findAll();
        verify(examenRepository, atLeast(1)).findAll();
        verify(examenRepository, atLeastOnce()).findAll();
        verify(examenRepository, atMost(10)).findAll();
        verify(examenRepository, atMostOnce()).findAll();

    }
}