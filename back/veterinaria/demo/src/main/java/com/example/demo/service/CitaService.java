package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.demo.dto.CitaResumenDto;
import com.example.demo.dto.SlotDisponibleDto;
import com.example.demo.entities.Cita;

public interface CitaService {
    Cita findById(Long id);
    List<Cita> findAll();
    List<Cita> findByVeterinarioId(Long veterinarioId);
    List<Cita> findByMascotaId(Long mascotaId);
    List<Cita> findByClienteId(Long clienteId);

    /** Admin crea una cita directamente (queda CONFIRMADA por defecto). */
    Cita save(Cita cita, Long clienteId, Long mascotaId, Long veterinarioId);

    /** Cliente solicita una cita (queda en PENDIENTE hasta que un admin la apruebe/rechace). */
    Cita solicitar(Cita cita, Long clienteId, Long mascotaId, Long veterinarioId);

    /** Admin aprueba una cita PENDIENTE. Transición → CONFIRMADA. */
    Cita aprobar(Long id);

    /** Admin rechaza una cita PENDIENTE con motivo opcional. Transición → CANCELADA. */
    Cita rechazar(Long id, String motivo);

    Cita update(Long id, Cita citaDetails);
    void cancelar(Long id);
    void delete(Long id);
    List<Cita> findCitasEnRango(LocalDateTime inicio, LocalDateTime fin);
    long contarPorRango(LocalDateTime inicio, LocalDateTime fin);
    List<CitaResumenDto> proximasEnRango(LocalDateTime inicio, LocalDateTime fin, int limite);

    /** Slots de 30 minutos libres en la jornada laboral del veterinario para un día concreto. */
    List<SlotDisponibleDto> disponibilidad(Long veterinarioId, LocalDate fecha);
}
