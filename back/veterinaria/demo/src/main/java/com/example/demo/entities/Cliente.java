package com.example.demo.entities;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
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

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cliente {

    // ID único autogenerado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del cliente (no vacío)
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    // Apellido del cliente (no vacío)
    @NotBlank(message = "El apellido no puede estar vacío")
    private String apellido;

    // Correo único y válido
    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "El correo debe contener '@' y un dominio válido")
    @Column(unique = true, nullable = false)
    private String correo;

    // Contraseña (no vacía)
    @NotBlank(message = "La contraseña no puede estar vacía")
    private String contrasenia;

    // Celular con mínimo 10 caracteres
    @NotBlank(message = "El celular no puede estar vacío")
    @Size(min = 10, message = "El celular debe tener al menos 10 caracteres")
    private String celular;

    // Lista de mascotas del cliente
    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)    
    private List<Mascota> mascotas = new ArrayList<>();

    // Lista de citas del cliente
    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Cita> citas = new ArrayList<>();
    
    // Constructor personalizado
    public Cliente(String nombre, String apellido, String correo, String contrasenia, String celular) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasenia = contrasenia;
        this.celular = celular;
        this.mascotas = new ArrayList<>();
        this.citas = new ArrayList<>();
    }
}