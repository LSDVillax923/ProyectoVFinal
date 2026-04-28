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
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Mascota {

    // ID único autogenerado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre de la mascota (no vacío)
    @NotBlank(message = "El nombre de la mascota no puede estar vacío")
    @Column(name = "mascota", nullable = false)
    private String nombre;
    
    // Especie (no vacía)
    @NotBlank(message = "La especie no puede estar vacía")
    private String especie;

    // Raza (no vacía)
    @NotBlank(message = "La raza no puede estar vacía")
    private String raza;

    // Sexo de la mascota
    private String sexo;
    
    // Fecha de nacimiento (obligatoria)
    @NotNull(message = "La fecha de nacimiento no puede estar vacía")
    private LocalDate fechaNacimiento;

    // Edad (mayor o igual a 0)
    @PositiveOrZero(message = "La edad debe ser un número positivo o cero")
    private int edad;

    // Peso (valor positivo)
    @Positive(message = "El peso debe ser un número positivo")
    private double peso;

    // URL o ruta de la foto
    private String foto;

    // Estado de la mascota (enum)
    @Enumerated(EnumType.STRING)
    private EstadoMascota estado; 
    
    // Información médica básica
    private String enfermedad;
    private String observaciones;
    private String tratamiento;
    private String veterinarioAsignado;
    
    // Relación con cliente (muchas mascotas pertenecen a un cliente)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnoreProperties({"mascotas", "citas"})
    private Cliente cliente;

    // Lista de tratamientos
    @JsonIgnore
    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Tratamiento> tratamientos = new ArrayList<>();

    // Lista de citas
    @JsonIgnore
    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Cita> citas = new ArrayList<>();

    // Constructor básico
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

    // Estados posibles de la mascota
    public enum EstadoMascota {
        ACTIVA, TRATAMIENTO, INACTIVA
    }
}