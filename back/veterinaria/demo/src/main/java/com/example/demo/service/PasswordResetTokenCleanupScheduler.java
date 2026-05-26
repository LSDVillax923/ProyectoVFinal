package com.example.demo.service;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.repository.PasswordResetTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Limpieza diaria de tokens de reset de contraseña.
 *  - Elimina los marcados como usados (ya cumplieron su función).
 *  - Elimina los que expiraron hace más de 7 días (basura útil para auditoría corta).
 * Se ejecuta todos los días a las 03:00 (hora servidor) para no chocar con tráfico.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PasswordResetTokenCleanupScheduler {

    private static final int RETENCION_DIAS = 7;

    private final PasswordResetTokenRepository tokenRepository;

    @Scheduled(cron = "0 0 3 * * *", zone = "America/Bogota")
    @Transactional
    public void limpiarTokensViejos() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(RETENCION_DIAS);
        int eliminados = tokenRepository.eliminarUsadosOExpirados(cutoff);
        if (eliminados > 0) {
            log.info("PasswordResetTokenCleanup: {} tokens eliminados (usados o expirados antes de {})",
                    eliminados, cutoff);
        }
    }
}
