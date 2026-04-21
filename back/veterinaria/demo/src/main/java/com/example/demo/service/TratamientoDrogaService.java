package com.example.demo.service;

import java.util.List;
import com.example.demo.entities.TratamientoDroga;

public interface TratamientoDrogaService {
    TratamientoDroga findById(Long id);
    List<TratamientoDroga> findByTratamientoId(Long tratamientoId);
    TratamientoDroga agregarDroga(Long tratamientoId, Long drogaId, int cantidad);
    void eliminarDroga(Long id);
}