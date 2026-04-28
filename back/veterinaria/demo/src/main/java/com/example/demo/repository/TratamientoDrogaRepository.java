package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.TratamientoDroga;

@Repository
public interface TratamientoDrogaRepository extends JpaRepository<TratamientoDroga, Long> {

    // Obtener drogas asociadas a un tratamiento
    List<TratamientoDroga> findByTratamientoId(Long tratamientoId);

    // Obtener tratamientos donde se usa una droga específica
    List<TratamientoDroga> findByDrogaId(Long drogaId);
}