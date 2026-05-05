package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;

import com.example.demo.dto.MedicamentoCantidadDto;
import com.example.demo.entities.TratamientoDroga;

/**
 * Servicio para gestionar drogas en tratamientos.
 */
public interface TratamientoDrogaService {

    /** Busca por ID */
    TratamientoDroga findById(Long id);

    /** Lista por tratamiento */
    List<TratamientoDroga> findByTratamientoId(Long tratamientoId);

    /** Agrega una droga a un tratamiento */
    TratamientoDroga agregarDroga(Long tratamientoId, Long drogaId, int cantidad);

    /** Elimina una droga del tratamiento */
    void eliminarDroga(Long id);

     /** KPI dashboard: medicamentos administrados en un rango de fechas, agrupados y ordenados desc */
    List<MedicamentoCantidadDto> medicamentosEntre(LocalDate inicio, LocalDate fin);
}