package com.example.demo.dto;

public record VeterinarioDto(
        Long id,
        String nombre,
        String cedula,
        String celular,
        String correo,
        String especialidad,
        String estado,
        Integer numAtenciones
) {}
