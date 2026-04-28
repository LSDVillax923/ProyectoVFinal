package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Tratamiento;

@Repository
public interface TratamientoRepository extends JpaRepository<Tratamiento, Long> {

    // Obtener todos los tratamientos con relaciones cargadas
    @Override
    @EntityGraph(attributePaths = {"mascota", "mascota.cliente", "veterinario", "drogas", "drogas.droga"})
    List<Tratamiento> findAll();

    // Obtener tratamientos ordenados por fecha ascendente
    @EntityGraph(attributePaths = {"mascota", "mascota.cliente", "veterinario", "drogas", "drogas.droga"})
    List<Tratamiento> findAllByOrderByFechaAsc();

    // Historial médico de una mascota
    List<Tratamiento> findByMascotaId(Long mascotaId);

    // Tratamientos realizados por un veterinario
    List<Tratamiento> findByVeterinarioId(Long veterinarioId);

    // Tratamientos dentro de un rango de fechas
    List<Tratamiento> findByFechaBetween(LocalDate inicio, LocalDate fin);

    // Tratamientos de un veterinario en un rango de fechas
    List<Tratamiento> findByVeterinarioIdAndFechaBetween(Long veterinarioId, LocalDate inicio, LocalDate fin);

    // Tratamientos programados (desde hoy en adelante)
    @EntityGraph(attributePaths = {"mascota", "mascota.cliente", "veterinario", "drogas", "drogas.droga"})
    @Query("SELECT t FROM Tratamiento t WHERE t.fecha >= CURRENT_DATE ORDER BY t.fecha ASC")
    List<Tratamiento> findTratamientosProgramados();
}