package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Envoltorio fino sobre JavaMailSender. Se separa para poder mockearlo en tests
 * y para centralizar el formato del email de recuperación.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    /**
     * Manda el correo de "olvidé mi contraseña" con el link de reset.
     * Si el envío falla (SMTP mal configurado) solo se loguea — no se propaga
     * al endpoint para no exponer si el correo existe.
     */
    public void enviarLinkReset(String correoDestino, String token) {
        String link = frontendUrl + "/inicio/reset-password?token=" + token;
        String texto = """
                Hola,

                Recibimos una solicitud para restablecer tu contraseña en MediCat.
                Si fuiste tú, abre el siguiente link (válido por 30 minutos):

                %s

                Si no solicitaste este cambio puedes ignorar este correo.

                — Equipo MediCat
                """.formatted(link);

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            if (fromAddress != null && !fromAddress.isBlank()) {
                msg.setFrom(fromAddress);
            }
            msg.setTo(correoDestino);
            msg.setSubject("MediCat — Restablecer contraseña");
            msg.setText(texto);
            mailSender.send(msg);
            log.info("Email de reset enviado a {}", correoDestino);
        } catch (Exception ex) {
            // Logueamos el link igual para que sea utilizable en desarrollo
            // aunque el SMTP no esté configurado todavía.
            log.warn("No se pudo enviar email a {} (SMTP no configurado?): {}", correoDestino, ex.getMessage());
            log.info("Link de reset (dev fallback) para {}: {}", correoDestino, link);
        }
    }
}
