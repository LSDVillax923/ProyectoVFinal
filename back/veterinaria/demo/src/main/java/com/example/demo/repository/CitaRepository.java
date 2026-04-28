package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Cita;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    // Obtener citas por ID del veterinario
    List<Cita> findByVeterinarioId(Long veterinarioId);

    // Obtener citas por ID de la mascota
    List<Cita> findByMascotaId(Long mascotaId);

    // Obtener citas por ID del cliente
    List<Cita> findByClienteId(Long clienteId);

    // Buscar citas que se solapan en un rango de fechas (validar disponibilidad)
    @Query("SELECT c FROM Cita c WHERE c.veterinario.id = :vetId AND " +
           "((c.fechaInicio BETWEEN :inicio AND :fin) OR (c.fechaFin BETWEEN :inicio AND :fin) OR " +
           "(c.fechaInicio <= :inicio AND c.fechaFin >= :fin))")
    List<Cita> findCitasSolapadas(@Param("vetId") Long vetId,
                                  @Param("inicio") LocalDateTime inicio,
                                  @Param("fin") LocalDateTime fin);
}