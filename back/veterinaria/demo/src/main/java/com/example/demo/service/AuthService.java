package com.example.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.LoginResponseDto;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.UserEntity;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponseDto login(LoginRequestDto request) {
             UserEntity user = buscarUsuario(request.getCorreo())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getContrasenia(), user.getContrasenia())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        String token = jwtService.generarToken(user.getCorreo(), user.getRol().name());
        return new LoginResponseDto(user.getId(), user.getNombre(), user.getCorreo(), user.getRol().name(), token);
    }

    private java.util.Optional<UserEntity> buscarUsuario(String identificador) {
        java.util.Optional<UserEntity> byCorreo = userRepository.findByCorreo(identificador);
        if (byCorreo.isPresent()) {
            return byCorreo;
        }

        if (identificador != null && !identificador.contains("@")) {
            java.util.Optional<Cliente> cliente = clienteRepository.findByCedula(identificador);
            if (cliente.isPresent()) {
                return userRepository.findByCorreo(cliente.get().getCorreo());
            }
        }

        return java.util.Optional.empty();
    }
}