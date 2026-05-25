package com.example.demo.dto;

public record TratamientoDrogaResumenDto(
        Long id,
        Long drogaId,
        String nombreDroga,
        int cantidad
) {}
