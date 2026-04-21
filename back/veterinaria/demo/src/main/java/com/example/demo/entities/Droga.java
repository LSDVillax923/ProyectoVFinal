package com.example.demo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Droga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @Positive(message = "El precio de compra debe ser un número positivo")
    private float precioCompra;
    
    @Positive(message = "El precio de venta debe ser un número positivo")
    private float precioVenta;

    @Min(value = 0, message = "Las unidades vendidas no pueden ser negativas")
    private int unidadesDisponibles;

    @Min(value = 0, message = "Las unidades vendidas no pueden ser negativas")
    private int unidadesVendidas;

}
