package com.example.demo.dto;

import java.time.LocalDate;

public record MascotaDto(
        Long id,
        String nombre,
        String especie,
        String raza,
        String sexo,
        LocalDate fechaNacimiento,
        Integer edad,
        Double peso,
        String foto,
        String estado,
        String enfermedad,
        String observaciones,
        String tratamiento,
        Long clienteId
) {}