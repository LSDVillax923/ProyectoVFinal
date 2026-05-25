package com.example.demo.entities;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Tratamiento {

    // ID único autogenerado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Diagnóstico del tratamiento
    private String diagnostico;

    // Observaciones (texto largo)
    @Column(length = 1000)
    private String observaciones;

    // Fecha del tratamiento (obligatoria)
    @NotNull(message = "La fecha no puede estar vacía")
    private LocalDate fecha;

    // Estado del tratamiento
    @Enumerated(EnumType.STRING)
    private EstadoTratamiento estado;  // PENDIENTE, COMPLETADO, CANCELADO

    // Relación con mascota
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mascota_id", nullable = false)
    @JsonIgnoreProperties({"tratamientos", "citas", "cliente", "hibernateLazyInitializer", "handler"})
    private Mascota mascota;

    // Relación con veterinario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id", nullable = false)
    @JsonIgnoreProperties({"tratamientos", "citas", "hibernateLazyInitializer", "handler"})
    private Veterinario veterinario;

    // Lista de drogas asociadas al tratamiento
    @JsonIgnore
    @OneToMany(mappedBy = "tratamiento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TratamientoDroga> drogas = new ArrayList<>();

    // Constructor básico
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

    // Estados posibles del tratamiento
    public enum EstadoTratamiento {
        PENDIENTE, COMPLETADO, CANCELADO
    }
}