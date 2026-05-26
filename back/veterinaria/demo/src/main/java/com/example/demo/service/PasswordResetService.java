package com.example.demo.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.Admin;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.PasswordResetToken;
import com.example.demo.entities.UserEntity;
import com.example.demo.entities.Veterinario;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.PasswordResetTokenRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VeterinarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final ClienteRepository clienteRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Value("${app.password-reset.expiration-minutes:30}")
    private int expirationMinutes;

    /**
     * Solicita un reset para el correo. Siempre se ejecuta sin lanzar errores aunque
     * el correo no exista — el controller responde 200 en ambos casos para no revelar
     * si una cuenta está registrada (defensa contra user-enumeration).
     */
    public void solicitarReset(String correo) {
        if (correo == null || correo.isBlank()) return;
        String correoNorm = correo.trim().toLowerCase();

        Optional<UserEntity> userOpt = userRepository.findByCorreo(correoNorm);
        if (userOpt.isEmpty()) {
            log.info("Solicitud de reset para correo no registrado: {}", correoNorm);
            return;
        }
        UserEntity user = userOpt.get();
        if (!user.isActivo()) {
            log.info("Solicitud de reset rechazada (cuenta inactiva): {}", correoNorm);
            return;
        }

        String token = generarToken();
        LocalDateTime expira = LocalDateTime.now().plusMinutes(expirationMinutes);
        tokenRepository.save(new PasswordResetToken(token, user, expira));

        mailService.enviarLinkReset(correoNorm, token);
    }

    /**
     * Aplica una nueva contraseña usando un token válido.
     * Lanza IllegalArgumentException si el token no existe, ya fue usado o expiró.
     */
    public void resetPassword(String token, String nuevaContrasenia) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token inválido");
        }
        if (nuevaContrasenia == null || nuevaContrasenia.isBlank() || nuevaContrasenia.length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres");
        }

        PasswordResetToken prt = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido o expirado"));

        if (!prt.esValido()) {
            throw new IllegalArgumentException("Token inválido o expirado");
        }

        UserEntity user = prt.getUserEntity();
        // Actualizamos el hash en la tabla central (la que usa /api/auth/login).
        user.setContrasenia(passwordEncoder.encode(nuevaContrasenia));
        userRepository.save(user);

        // Mantenemos sincronizada la contraseña plaintext del perfil específico para que
        // los endpoints legacy /api/clientes/login, /api/veterinarios/login, /api/admins/login
        // sigan funcionando con la nueva contraseña.
        sincronizarPerfilEspecifico(user, nuevaContrasenia);

        prt.setUsed(true);
        tokenRepository.save(prt);

        log.info("Contraseña restablecida exitosamente para {}", user.getCorreo());
    }

    private void sincronizarPerfilEspecifico(UserEntity user, String plaintext) {
        switch (user.getRol()) {
            case CLIENTE -> {
                Cliente c = user.getCliente();
                if (c != null) {
                    c.setContrasenia(plaintext);
                    clienteRepository.save(c);
                }
            }
            case VETERINARIO -> {
                Veterinario v = user.getVeterinario();
                if (v != null) {
                    v.setContrasenia(plaintext);
                    veterinarioRepository.save(v);
                }
            }
            case ADMIN -> {
                Admin a = user.getAdmin();
                if (a != null) {
                    a.setContrasenia(plaintext);
                    adminRepository.save(a);
                }
            }
        }
    }

    /** Token URL-safe de 32 bytes → 43 caracteres base64. */
    private String generarToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
