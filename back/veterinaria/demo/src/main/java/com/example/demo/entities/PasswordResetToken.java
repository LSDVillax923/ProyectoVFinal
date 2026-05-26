package com.example.demo.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token de un solo uso para que un usuario pueda restablecer su contraseña.
 *  - Se genera al solicitar /api/auth/forgot-password.
 *  - Expira a los {@code app.password-reset.expiration-minutes} minutos.
 *  - Se marca como {@code used=true} apenas el usuario completa /api/auth/reset-password.
 */
@Entity
@Data
@NoArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Token aleatorio (URL-safe, ~64 caracteres) que viaja en el link del email. */
    @Column(nullable = false, unique = true, length = 128)
    private String token;

    /** Usuario al que pertenece el token. Se asocia por id para no acoplar al correo (que puede cambiar). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_entity_id", nullable = false)
    private UserEntity userEntity;

    /** Copia del correo al momento de generar el token. Solo para depuración/auditoría. */
    @Column(nullable = false)
    private String correo;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public PasswordResetToken(String token, UserEntity userEntity, LocalDateTime expiresAt) {
        this.token = token;
        this.userEntity = userEntity;
        this.correo = userEntity.getCorreo();
        this.expiresAt = expiresAt;
        this.used = false;
        this.createdAt = LocalDateTime.now();
    }

    /** True si el token todavía es utilizable (no usado y no expirado). */
    public boolean esValido() {
        return !used && expiresAt != null && LocalDateTime.now().isBefore(expiresAt);
    }
}
