package com.example.demo.dto;

public record ClienteDto(
        Long id,
        String nombre,
        String apellido,
        String cedula,
        String correo,
        String contrasenia,
        String celular
) {}