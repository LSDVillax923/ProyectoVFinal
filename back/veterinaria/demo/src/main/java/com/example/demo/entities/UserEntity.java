package com.example.demo.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Tabla central de usuarios del sistema.
 * Todos los tipos de usuario (ADMIN, VETERINARIO, CLIENTE) quedan
 * mapeados aquí, y cada fila apunta opcionalmente al perfil específico.
 *
 * IMPORTANTE: la contraseña debe almacenarse cifrada con BCrypt.
 */
@Entity
@Table(name = "user_entity")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Correo que se usa como nombre de usuario para autenticar. */
    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "El correo debe tener formato válido")
    @Column(unique = true, nullable = false)
    private String correo;

    /** Contraseña cifrada con BCrypt. */
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Column(nullable = false)
    private String contrasenia;

    /** Rol del usuario: ADMIN, VETERINARIO o CLIENTE. */
    @NotNull(message = "El rol es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol;

    /** Nombre para mostrar (copia del perfil específico). */
    private String nombre;

    /** Indica si la cuenta está habilitada. */
    @Column(nullable = false)
    private boolean activo = true;

    // ── Relaciones opcionales con los perfiles ──────────────────────────────

    /**
     * Si rol == ADMIN, apunta al perfil Admin.
     * nullable porque VETERINARIO y CLIENTE no lo usan.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = true)
    private Admin admin;

    /**
     * Si rol == VETERINARIO, apunta al perfil Veterinario.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinario_id", nullable = true)
    private Veterinario veterinario;

    /**
     * Si rol == CLIENTE, apunta al perfil Cliente.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = true)
    private Cliente cliente;

    public boolean isActivo() {
        return this.activo;
    }

    public String getContrasenia() {
        return this.contrasenia;
    }

    // ── Enum de roles ───────────────────────────────────────────────────────

    public enum RolUsuario {
        ADMIN,
        VETERINARIO,
        CLIENTE
    }

    // ── Constructores de fábrica ─────────────────────────────────────────────

    /** Crea un UserEntity para un Admin ya persistido. */
    public static UserEntity deAdmin(Admin admin, String contraseniaHash) {
        UserEntity u = new UserEntity();
        u.setCorreo(admin.getCorreo());
        u.setContrasenia(contraseniaHash);
        u.setRol(RolUsuario.ADMIN);
        u.setNombre(admin.getNombre());
        u.setAdmin(admin);
        u.setActivo(true);
        return u;
    }

    /** Crea un UserEntity para un Veterinario ya persistido. */
    public static UserEntity deVeterinario(Veterinario vet, String contraseniaHash) {
        UserEntity u = new UserEntity();
        u.setCorreo(vet.getCorreo());
        u.setContrasenia(contraseniaHash);
        u.setRol(RolUsuario.VETERINARIO);
        u.setNombre(vet.getNombre());
        u.setVeterinario(vet);
        u.setActivo(true);
        return u;
    }

    /** Crea un UserEntity para un Cliente ya persistido. */
    public static UserEntity deCliente(Cliente cliente, String contraseniaHash) {
        UserEntity u = new UserEntity();
        u.setCorreo(cliente.getCorreo());
        u.setContrasenia(contraseniaHash);
        u.setRol(RolUsuario.CLIENTE);
        u.setNombre(cliente.getNombre() + " " + cliente.getApellido());
        u.setCliente(cliente);
        u.setActivo(true);
        return u;
    }
}
