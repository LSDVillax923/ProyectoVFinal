package com.example.demo.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Veterinario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    private String cedula;

    @Size(min = 10, message = "El celular debe tener al menos 10 caracteres")
    private String celular;

    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "El correo debe contener '@' y un dominio válido")
    @Column(unique = true, nullable = false)    
    private String correo;

    private String especialidad;

    // Contraseña: WRITE_ONLY (aceptada en POST/PUT, nunca serializada al frontend).
    // No se valida con @NotBlank porque en el PUT puede llegar vacía si el admin no
    // está cambiando la contraseña; la obligatoriedad en el create vive en el service.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String contrasenia;
    private String estado;
    private int numAtenciones;

    @JsonIgnore
    @OneToMany(mappedBy = "veterinario")
    private List<Tratamiento> tratamientos;

    @JsonIgnore
    @OneToMany(mappedBy = "veterinario", fetch = FetchType.LAZY)
    private List<Cita> citas = new ArrayList<>();    

    public Veterinario(String nombre, String cedula, String celular, String correo,
                       String especialidad, String contrasenia, String estado) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.celular = celular;
        this.correo = correo;
        this.especialidad = especialidad;
        this.contrasenia = contrasenia;
        this.estado = estado;
        this.tratamientos = new ArrayList<>();
        this.citas = new ArrayList<>();
    }
}