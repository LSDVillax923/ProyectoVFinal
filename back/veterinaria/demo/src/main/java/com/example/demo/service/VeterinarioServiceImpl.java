package com.example.demo.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.entities.UserEntity;
import com.example.demo.entities.Veterinario;
import com.example.demo.errors.VeterinarioException;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VeterinarioRepository;

@Service
@Transactional
public class VeterinarioServiceImpl implements VeterinarioService {

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Veterinario findById(Long id) {
        return veterinarioRepository.findById(id)
                .orElseThrow(() -> new VeterinarioException("Veterinario no encontrado con ID: " + id));
    }

    @Override
    public List<Veterinario> findAll() {
        return veterinarioRepository.findAll();
    }

    @Override
    public List<Veterinario> findActivos() {
        return veterinarioRepository.findByEstado("activo");
    }

    @Override
    public Veterinario save(Veterinario veterinario) {
        validarVeterinario(veterinario);
        if (veterinario.getId() == null) {
            if (veterinarioRepository.existsByCorreo(veterinario.getCorreo())) {
                throw new IllegalArgumentException("Correo ya registrado");
            }
            if (veterinario.getCedula() != null && veterinarioRepository.existsByCedula(veterinario.getCedula())) {
                throw new IllegalArgumentException("Cédula ya registrada");
            }
            // En la creación la contraseña sí es obligatoria.
            if (veterinario.getContrasenia() == null || veterinario.getContrasenia().isBlank()) {
                throw new IllegalArgumentException("La contraseña es obligatoria al crear el veterinario");
            }
        }
        if (veterinario.getEstado() == null) {
            veterinario.setEstado("activo");
        }

        Veterinario guardado = veterinarioRepository.save(veterinario);
        sincronizarUsuarioVeterinario(guardado, veterinario.getContrasenia());
        return guardado;
    }

    @Override
    public Veterinario update(Long id, Veterinario veterinarioDetails) {
        Veterinario existing = findById(id);
        existing.setNombre(veterinarioDetails.getNombre());
        existing.setCedula(veterinarioDetails.getCedula());
        existing.setCelular(veterinarioDetails.getCelular());
        existing.setCorreo(veterinarioDetails.getCorreo());
        existing.setEspecialidad(veterinarioDetails.getEspecialidad());
        // La contraseña solo se actualiza si llega una nueva (no vacía); de lo contrario
        // se conserva la existente. El backend ya no expone la contraseña al frontend
        // (WRITE_ONLY) así que el form la envía vacía cuando el admin no la cambia.
        String contraseniaPlana = null;
        if (veterinarioDetails.getContrasenia() != null && !veterinarioDetails.getContrasenia().isBlank()) {
            existing.setContrasenia(veterinarioDetails.getContrasenia());
            contraseniaPlana = veterinarioDetails.getContrasenia();
        }
        Veterinario actualizado = veterinarioRepository.save(existing);
        sincronizarUsuarioVeterinario(actualizado, contraseniaPlana);
        return actualizado;
    }

    @Override
    public void cambiarEstado(Long id, String nuevoEstado) {
        Veterinario vet = findById(id);
        vet.setEstado(nuevoEstado);
        veterinarioRepository.save(vet);

        // Mantener UserEntity.activo coherente con el estado del perfil para que el
        // login central refleje la baja/alta sin tener que re-encolar contraseñas.
        userRepository.findByCorreo(vet.getCorreo()).ifPresent(user -> {
            user.setActivo("activo".equalsIgnoreCase(nuevoEstado));
            userRepository.save(user);
        });
    }

    @Override
    public void delete(Long id) {
        Veterinario vet = findById(id);
        userRepository.findByCorreo(vet.getCorreo()).ifPresent(userRepository::delete);
        veterinarioRepository.delete(vet);
    }

    @Override
    public Veterinario login(String correo, String contrasenia) {
        // Login legacy (texto plano). Mantenido por compatibilidad con el endpoint
        // /api/veterinarios/login. La autenticación real ocurre en /api/auth/login.
        Veterinario vet = veterinarioRepository.findByCorreo(correo)
                .filter(v -> v.getContrasenia() != null && v.getContrasenia().equals(contrasenia))
                .orElse(null);
        if (vet == null) {
            return null;
        }
        if (vet.getEstado() != null && vet.getEstado().equalsIgnoreCase("inactivo")) {
            throw new IllegalArgumentException("El veterinario está desactivado y no puede iniciar sesión.");
        }
        return vet;
    }

    @Override
    public void incrementarAtenciones(Long id) {
        Veterinario vet = findById(id);
        vet.setNumAtenciones(vet.getNumAtenciones() + 1);
        veterinarioRepository.save(vet);
    }

    @Override
    public long contarPorEstado(String estado) {
        return veterinarioRepository.countByEstadoIgnoreCase(estado);
    }

    private void validarVeterinario(Veterinario v) {
        if (v.getNombre() == null || v.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (v.getCorreo() == null || !v.getCorreo().contains("@")) {
            throw new IllegalArgumentException("Correo inválido");
        }
        // Nota: la obligatoriedad de la contraseña en el create se valida en save(); no se
        // chequea aquí porque este método también se ejecuta en updates.
    }

    /**
     * Crea o actualiza el UserEntity asociado al veterinario para que el login
     * central por /api/auth/login funcione. Sin este paso el perfil Veterinario
     * existe pero nunca aparece en user_entity → AuthService devuelve 400.
     */
    private void sincronizarUsuarioVeterinario(Veterinario vet, String contraseniaPlana) {
        userRepository.findByCorreo(vet.getCorreo()).ifPresentOrElse(user -> {
            user.setNombre(vet.getNombre());
            user.setVeterinario(vet);
            user.setRol(UserEntity.RolUsuario.VETERINARIO);
            user.setActivo(!"inactivo".equalsIgnoreCase(vet.getEstado()));
            if (contraseniaPlana != null && !contraseniaPlana.isBlank()) {
                user.setContrasenia(passwordEncoder.encode(contraseniaPlana));
            }
            userRepository.save(user);
        }, () -> {
            String hash = (contraseniaPlana != null && !contraseniaPlana.isBlank())
                    ? passwordEncoder.encode(contraseniaPlana)
                    : passwordEncoder.encode(vet.getContrasenia());
            UserEntity nuevo = UserEntity.deVeterinario(vet, hash);
            nuevo.setActivo(!"inactivo".equalsIgnoreCase(vet.getEstado()));
            userRepository.save(nuevo);
        });
    }
}
