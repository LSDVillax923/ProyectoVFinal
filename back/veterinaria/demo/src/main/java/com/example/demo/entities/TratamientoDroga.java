package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class TratamientoDroga {

    // ID único autogenerado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con tratamiento (obligatoria)
    @NotNull(message = "La línea debe pertenecer a un tratamiento")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tratamiento_id", nullable = false)
    @JsonIgnore
    private Tratamiento tratamiento;

    // Relación con droga (obligatoria)
    @NotNull(message = "Debe indicarse una droga")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "droga_id", nullable = false)
    private Droga droga;

    // Cantidad de la droga (mínimo 1)
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private int cantidad;

    // Constructor básico
    public TratamientoDroga(Tratamiento tratamiento, Droga droga, int cantidad) {
        this.tratamiento = tratamiento;
        this.droga = droga;
        this.cantidad = cantidad;
    }
}