package com.example.demo.dto;

import java.time.LocalDate;

public record TratamientoDto(
        Long id,
        String diagnostico,
        String observaciones,
        LocalDate fecha,
        String estado,
        Long mascotaId,
        Long veterinarioId
) {}