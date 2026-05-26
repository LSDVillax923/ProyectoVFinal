package com.example.demo.dto;

import java.time.LocalDateTime;

public record CitaDetalleDto(
        Long id,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        String motivo,
        String estado,
        String motivoRechazo,
        Long clienteId,
        String clienteNombre,
        Long mascotaId,
        String mascotaNombre,
        Long veterinarioId,
        String veterinarioNombre
) {}
