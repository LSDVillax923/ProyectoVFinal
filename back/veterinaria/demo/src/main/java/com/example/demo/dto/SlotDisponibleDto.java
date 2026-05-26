package com.example.demo.dto;

import java.time.LocalDateTime;

/** Slot de 30 minutos libre en la agenda de un veterinario. */
public record SlotDisponibleDto(
        LocalDateTime inicio,
        LocalDateTime fin
) {}
