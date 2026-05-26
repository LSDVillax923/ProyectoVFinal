package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.PasswordResetToken;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Borra tokens que ya no aportan nada:
     *  - Ya usados (terminaron su ciclo de vida).
     *  - Vencidos hace más de {@code cutoff} (basura que nunca se usó).
     * Devuelve la cantidad de filas eliminadas para poder loguearlas.
     */
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.used = true OR t.expiresAt < :cutoff")
    int eliminarUsadosOExpirados(@Param("cutoff") LocalDateTime cutoff);
}
