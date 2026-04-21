package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
public class TratamientoDroga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La línea debe pertenecer a un tratamiento")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tratamiento_id", nullable = false)
    @JsonIgnore   // No serializar el tratamiento completo
    private Tratamiento tratamiento;

    @NotNull(message = "Debe indicarse una droga")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "droga_id", nullable = false)
    private Droga droga;   // Se serializará completo (id, nombre, etc.)

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private int cantidad;

    public TratamientoDroga(Tratamiento tratamiento, Droga droga, int cantidad) {
        this.tratamiento = tratamiento;
        this.droga = droga;
        this.cantidad = cantidad;
    }
}