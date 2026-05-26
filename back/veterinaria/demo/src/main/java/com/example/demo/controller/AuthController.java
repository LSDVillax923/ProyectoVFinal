package com.example.demo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ForgotPasswordRequestDto;
import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.LoginResponseDto;
import com.example.demo.dto.ResetPasswordRequestDto;
import com.example.demo.service.AuthService;
import com.example.demo.service.PasswordResetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Inicia el flujo de recuperación: si el correo existe se genera un token y
     * se envía un email con el link. La respuesta siempre es 200 — no revela si
     * el correo está registrado (defensa contra user-enumeration).
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDto request) {
        passwordResetService.solicitarReset(request.getCorreo());
        return ResponseEntity.ok(Map.of(
                "mensaje", "Si el correo está registrado recibirás un email con instrucciones."
        ));
    }

    /**
     * Completa el flujo: aplica la nueva contraseña usando el token recibido por email.
     * Devuelve 400 si el token no es válido o expiró.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
        passwordResetService.resetPassword(request.getToken(), request.getContrasenia());
        return ResponseEntity.ok(Map.of(
                "mensaje", "Contraseña actualizada correctamente. Ya puedes iniciar sesión."
        ));
    }
}
