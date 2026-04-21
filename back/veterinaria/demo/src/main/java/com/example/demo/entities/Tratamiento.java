package com.example.demo.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Data
@NoArgsConstructor
public class Tratamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String diagnostico;

    @Column(length = 1000)
    private String observaciones;

    @NotNull(message = "La fecha no puede estar vacía")
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    private EstadoTratamiento estado;  // PENDIENTE, COMPLETADO, CANCELADO

    // Relación con Mascota
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mascota_id", nullable = false)
    @JsonIgnoreProperties({"tratamientos", "citas", "cliente"})
    private Mascota mascota;

    // Relación con Veterinario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id", nullable = false)
    @JsonIgnoreProperties({"tratamientos", "citas"})
    private Veterinario veterinario;

    // Relación con TratamientoDroga
    @OneToMany(mappedBy = "tratamiento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TratamientoDroga> drogas = new ArrayList<>();

    public Tratamiento(String diagnostico, String observaciones, LocalDate fecha,
                       EstadoTratamiento estado, Mascota mascota, Veterinario veterinario) {
        this.diagnostico = diagnostico;
        this.observaciones = observaciones;
        this.fecha = fecha;
        this.estado = estado;
        this.mascota = mascota;
        this.veterinario = veterinario;
        this.drogas = new ArrayList<>();
    }

    public enum EstadoTratamiento {
        PENDIENTE, COMPLETADO, CANCELADO
    }
}