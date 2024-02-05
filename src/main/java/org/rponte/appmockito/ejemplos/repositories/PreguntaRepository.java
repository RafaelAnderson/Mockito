package org.rponte.appmockito.ejemplos.repositories;

import java.util.List;

public interface PreguntaRepository {
    List<String> findPreguntasPorExamen(Long id);

    void guardarVarias(List<String> preguntas);
}
