package com.example.demo.service;

import java.util.List;
import com.example.demo.entities.Tratamiento;

public interface TratamientoService {
    Tratamiento findById(Long id);
    List<Tratamiento> findAll();
    List<Tratamiento> findByMascotaId(Long mascotaId);
    List<Tratamiento> findByVeterinarioId(Long veterinarioId);
    Tratamiento save(Tratamiento tratamiento, Long mascotaId, Long veterinarioId);
    Tratamiento update(Long id, Tratamiento tratamientoDetails);
    void delete(Long id);
    List<Tratamiento> findProgramados();
}