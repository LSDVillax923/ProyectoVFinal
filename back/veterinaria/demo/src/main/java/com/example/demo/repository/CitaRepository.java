package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.Collection;
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

    /**
     * Citas del veterinario cuyo intervalo se cruza con [inicio, fin). Usa la regla
     * estándar de solapamiento abierto: A overlaps B  iff  A.inicio < B.fin AND B.inicio < A.fin.
     * Esto permite que dos citas adyacentes (ej. 9:00-9:30 y 9:30-10:00) NO se consideren
     * solapadas — únicamente las que realmente se cruzan.
     * El estado CANCELADA se filtra en el servicio para que un slot liberado vuelva a quedar libre.
     */
    @Query("SELECT c FROM Cita c WHERE c.veterinario.id = :vetId " +
           "AND c.fechaInicio < :fin AND c.fechaFin > :inicio")
    List<Cita> findCitasSolapadas(@Param("vetId") Long vetId,
                                  @Param("inicio") LocalDateTime inicio,
                                  @Param("fin") LocalDateTime fin);

    // KPI dashboard: cuenta citas dentro de un rango (ej. citas de hoy)
    long countByFechaInicioBetween(LocalDateTime inicio, LocalDateTime fin);

    // KPI dashboard: próximas citas en un rango con estados específicos, ordenadas asc
    List<Cita> findByFechaInicioBetweenAndEstadoInOrderByFechaInicioAsc(
            LocalDateTime inicio,
            LocalDateTime fin,
            Collection<Cita.EstadoCita> estados);
}