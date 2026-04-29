package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import com.example.demo.dto.MedicamentoCantidadDto;
import com.example.demo.entities.TratamientoDroga;

public interface TratamientoDrogaService {
    TratamientoDroga findById(Long id);
    List<TratamientoDroga> findByTratamientoId(Long tratamientoId);
    TratamientoDroga agregarDroga(Long tratamientoId, Long drogaId, int cantidad);
    void eliminarDroga(Long id);

    /** KPI dashboard: medicamentos administrados en un rango de fechas, agrupados y ordenados desc */
    List<MedicamentoCantidadDto> medicamentosEntre(LocalDate inicio, LocalDate fin);
}