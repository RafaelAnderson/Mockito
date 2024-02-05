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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplSpyTest {
    @Spy
    ExamenRepositoryImpl examenRepository;
    @Spy
    PreguntaRepositoryImpl preguntaRepository;
    @InjectMocks
    ExamenServiceImpl examenService;

    @Test
    void testSpy() {
        // Mock simula los métodos, Spy requiere que se cree a partir de una clase concreta
        // no desde una clase abstracta
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
}