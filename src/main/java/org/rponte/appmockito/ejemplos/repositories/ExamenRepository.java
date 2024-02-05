package org.rponte.appmockito.ejemplos.repositories;

import org.rponte.appmockito.ejemplos.models.Examen;

import java.util.List;

public interface ExamenRepository {
    Examen guardar(Examen examen);

    List<Examen> findAll();
}
