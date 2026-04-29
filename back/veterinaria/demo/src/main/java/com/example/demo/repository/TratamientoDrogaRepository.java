package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.MedicamentoCantidadDto;
import com.example.demo.entities.TratamientoDroga;

@Repository
public interface TratamientoDrogaRepository extends JpaRepository<TratamientoDroga, Long> {

    // Obtener drogas asociadas a un tratamiento
    List<TratamientoDroga> findByTratamientoId(Long tratamientoId);

    // Obtener tratamientos donde se usa una droga específica
    List<TratamientoDroga> findByDrogaId(Long drogaId);

    // KPI dashboard: medicamentos administrados (suma de cantidades) en un rango,
    // agrupados por nombre de droga y ordenados desc por cantidad.
    @Query("""
            SELECT new com.example.demo.dto.MedicamentoCantidadDto(td.droga.nombre, SUM(td.cantidad))
            FROM TratamientoDroga td
            WHERE td.tratamiento.fecha BETWEEN :inicio AND :fin
            GROUP BY td.droga.nombre
            ORDER BY SUM(td.cantidad) DESC
           """)
    List<MedicamentoCantidadDto> sumarMedicamentosEntre(
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);
}