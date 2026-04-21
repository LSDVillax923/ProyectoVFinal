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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la mascota no puede estar vacío")
    @Column(name = "mascota", nullable = false)
    private String nombre;
    
    @NotBlank(message = "La especie no puede estar vacía")
    private String especie;

    @NotBlank(message = "La raza no puede estar vacía")
    private String raza;

    private String sexo;       
    
    @NotNull(message = "La fecha de nacimiento no puede estar vacía")
    private LocalDate fechaNacimiento;

    @Positive(message = "La edad debe ser un número positivo")
    private int edad;

    @Positive(message = "El peso debe ser un número positivo")
    private double peso;

    private String foto;

    @Enumerated(EnumType.STRING)
    private EstadoMascota estado; 
    
    private String enfermedad;
    private String observaciones;
    private String tratamiento;
    private String veterinarioAsignado;
    
    // Relación con Cliente (lado propietario)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnoreProperties({"mascotas", "citas"})   // Evita serializar las listas del cliente
    private Cliente cliente;

    // Relación con Tratamiento
    @JsonIgnore
    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Tratamiento> tratamientos = new ArrayList<>();

    // Relación con Cita 
    @JsonIgnore
    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Cita> citas = new ArrayList<>();

    // Constructor básico (sin colecciones)
    public Mascota(String nombre, String especie, String raza, String sexo, LocalDate fechaNacimiento,
                   int edad, double peso, String foto, EstadoMascota estado, String enfermedad,
                   String observaciones, Cliente cliente) {
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.sexo = sexo;
        this.fechaNacimiento = fechaNacimiento;
        this.edad = edad;
        this.peso = peso;
        this.foto = foto;
        this.estado = estado;
        this.enfermedad = enfermedad;
        this.observaciones = observaciones;
        this.cliente = cliente;
        this.tratamientos = new ArrayList<>();
        this.citas = new ArrayList<>();
    }

    // Enum interno para estado
    public enum EstadoMascota {
        ACTIVA, TRATAMIENTO, INACTIVA
    }
}
