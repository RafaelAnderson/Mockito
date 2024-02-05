package org.rponte.appmockito.ejemplos.repositories;

import org.rponte.appmockito.ejemplos.Datos;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PreguntaRepositoryImpl implements PreguntaRepository {
    @Override
    public List<String> findPreguntasPorExamen(Long id) {
        System.out.println("PreguntaRepositoryImpl.findPreguntasPorExamen");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Datos.PREGUNTAS;
    }

    @Override
    public void guardarVarias(List<String> preguntas) {
        System.out.println("PreguntaRepositoryImpl.guardarVarias");
    }
}
