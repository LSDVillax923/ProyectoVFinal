package com.example.demo.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Data
@NoArgsConstructor
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;

    @NotBlank(message = "El motivo no puede estar vacío")
    private String motivo;

    @Enumerated(EnumType.STRING)
    private EstadoCita estado = EstadoCita.PENDIENTE;

    // Relación con Cliente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnoreProperties({"mascotas", "citas"})
    private Cliente cliente;

    // Relación con Mascota
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mascota_id", nullable = false)
    @JsonIgnoreProperties({"cliente", "tratamientos", "citas"})
    private Mascota mascota;

    // Relación con Veterinario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id", nullable = false)
    @JsonIgnoreProperties({"tratamientos", "citas"})
    private Veterinario veterinario;

    public Cita(LocalDateTime fechaInicio, LocalDateTime fechaFin, String motivo,
                EstadoCita estado, Cliente cliente, Mascota mascota, Veterinario veterinario) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.motivo = motivo;
        this.estado = estado;
        this.cliente = cliente;
        this.mascota = mascota;
        this.veterinario = veterinario;
    }

    public enum EstadoCita {
        PENDIENTE, CONFIRMADA, REALIZADA, CANCELADA
    }
}