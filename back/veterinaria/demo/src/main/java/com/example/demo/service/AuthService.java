package com.example.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.LoginResponseDto;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.UserEntity;
import com.example.demo.entities.Veterinario;
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

        // Bloqueo de cuentas: cualquier UserEntity con activo=false no puede entrar.
        if (!user.isActivo()) {
            throw new IllegalArgumentException("Tu cuenta está desactivada. Contacta al administrador.");
        }

        // Caso especial veterinario: el "estado" se guarda en el perfil Veterinario.
        if (user.getRol() == UserEntity.RolUsuario.VETERINARIO) {
            Veterinario vet = user.getVeterinario();
            if (vet == null || vet.getEstado() == null || !"activo".equalsIgnoreCase(vet.getEstado())) {
                throw new IllegalArgumentException("Veterinario desactivado. Contacta al administrador para reactivar tu cuenta.");
            }
        }

        String token = jwtService.generarToken(user.getCorreo(), user.getRol().name());

        // IMPORTANTE: el id que viaja al frontend debe ser el id del perfil específico
        // (Cliente/Veterinario/Admin), no el id de UserEntity, porque todos los endpoints
        // posteriores (findByClienteId, findByVeterinarioId, etc.) buscan por ese id.
        Long idDelRol = resolverIdDelRol(user);

        return new LoginResponseDto(idDelRol, user.getNombre(), user.getCorreo(), user.getRol().name(), token);
    }

    private Long resolverIdDelRol(UserEntity user) {
        return switch (user.getRol()) {
            case ADMIN       -> user.getAdmin()       != null ? user.getAdmin().getId()       : user.getId();
            case VETERINARIO -> user.getVeterinario() != null ? user.getVeterinario().getId() : user.getId();
            case CLIENTE     -> user.getCliente()     != null ? user.getCliente().getId()     : user.getId();
        };
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
