package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin {

    // ID único autogenerado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del administrador (no vacío)
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    // Correo único y válido
    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "El correo debe contener '@' y un dominio válido")
    @Column(unique = true, nullable = false)
    private String correo;

    // Contraseña (no vacía)
    @NotBlank(message = "La contraseña no puede estar vacía")
    private String contrasenia;
}