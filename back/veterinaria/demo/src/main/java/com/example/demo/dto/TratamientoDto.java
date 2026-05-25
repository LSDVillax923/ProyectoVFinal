package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

public record TratamientoDto(
        Long id,
        String diagnostico,
        String observaciones,
        LocalDate fecha,
        String estado,
        Long mascotaId,
        String mascotaNombre,
        Long clienteId,
        Long veterinarioId,
        String veterinarioNombre,
        List<TratamientoDrogaResumenDto> drogas
) {}
