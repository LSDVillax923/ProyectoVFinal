package com.example.demo.dto;

import java.time.LocalDateTime;

public record NotificacionDto(
        Long id,
        Long clienteId,
        String tipo,        // APROBADA / RECHAZADA / RECORDATORIO
        String mensaje,
        boolean leida,
        Long citaId,
        LocalDateTime createdAt
) {}
