package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import com.example.demo.entities.Cita;

public interface CitaService {
    Cita findById(Long id);
    List<Cita> findAll();
    List<Cita> findByVeterinarioId(Long veterinarioId);
    List<Cita> findByMascotaId(Long mascotaId);
    List<Cita> findByClienteId(Long clienteId);
    Cita save(Cita cita, Long clienteId, Long mascotaId, Long veterinarioId);
    Cita update(Long id, Cita citaDetails);
    void cancelar(Long id);
    void delete(Long id);
    List<Cita> findCitasEnRango(LocalDateTime inicio, LocalDateTime fin);
}