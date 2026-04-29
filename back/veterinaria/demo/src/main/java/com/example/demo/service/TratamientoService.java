package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;

import com.example.demo.entities.Tratamiento;

/**
 * Servicio para gestionar tratamientos.
 */
public interface TratamientoService {

    /** Busca un tratamiento por ID */
    Tratamiento findById(Long id);

    /** Lista todos los tratamientos */
    List<Tratamiento> findAll();

    /** Lista tratamientos por mascota */
    List<Tratamiento> findByMascotaId(Long mascotaId);

    /** Lista tratamientos por veterinario */
    List<Tratamiento> findByVeterinarioId(Long veterinarioId);

    /** Guarda un tratamiento */
    Tratamiento save(Tratamiento tratamiento, Long mascotaId, Long veterinarioId);

    /** Actualiza un tratamiento */
    Tratamiento update(Long id, Tratamiento tratamientoDetails);

    /** Elimina un tratamiento */
    void delete(Long id);

    /** Lista tratamientos programados */
    List<Tratamiento> findProgramados();

    /** KPI dashboard: cuenta tratamientos cuya fecha cae en el rango (incluyente) */
    long contarPorRango(LocalDate inicio, LocalDate fin);
}