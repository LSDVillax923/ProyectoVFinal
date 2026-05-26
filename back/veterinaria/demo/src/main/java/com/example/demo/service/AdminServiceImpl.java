package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.Admin;
import com.example.demo.entities.UserEntity;
import com.example.demo.errors.AdminException;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.UserRepository;

/**
 * Implementación del servicio de administradores.
 */
@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /** Busca un admin por ID */
    @Override
    public Admin findById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new AdminException("Admin no encontrado con ID: " + id));
    }

    /** Lista todos los admins */
    @Override
    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    /** Guarda un admin con validaciones básicas */
    @Override
    public Admin save(Admin admin) {
        if (admin.getNombre() == null || admin.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (admin.getCorreo() == null || !admin.getCorreo().contains("@")) {
            throw new IllegalArgumentException("Correo inválido");
        }
        if (admin.getId() == null) {
            if (adminRepository.existsByCorreo(admin.getCorreo())) {
                throw new IllegalArgumentException("Correo ya registrado");
            }
            if (admin.getContrasenia() == null || admin.getContrasenia().isBlank()) {
                throw new IllegalArgumentException("La contraseña es obligatoria al crear el administrador");
            }
        }
        Admin guardado = adminRepository.save(admin);
        sincronizarUsuarioAdmin(guardado, admin.getContrasenia());
        return guardado;
    }

    /** Actualiza un admin */
    @Override
    public Admin update(Long id, Admin adminDetails) {
        Admin existing = findById(id);
        existing.setNombre(adminDetails.getNombre());
        existing.setCorreo(adminDetails.getCorreo());

        // La contraseña solo se actualiza si llega una nueva (no vacía).
        String contraseniaPlana = null;
        if (adminDetails.getContrasenia() != null && !adminDetails.getContrasenia().isBlank()) {
            existing.setContrasenia(adminDetails.getContrasenia());
            contraseniaPlana = adminDetails.getContrasenia();
        }
        Admin actualizado = adminRepository.save(existing);
        sincronizarUsuarioAdmin(actualizado, contraseniaPlana);
        return actualizado;
    }

    /** Elimina un admin */
    @Override
    public void delete(Long id) {
        Admin admin = findById(id);
        userRepository.findByCorreo(admin.getCorreo()).ifPresent(userRepository::delete);
        adminRepository.delete(admin);
    }

    /** Login de admin (legacy: texto plano). El login real corre por /api/auth/login. */
    @Override
    public Admin login(String correo, String contrasenia) {
        return adminRepository.findByCorreo(correo)
                .filter(a -> a.getContrasenia() != null && a.getContrasenia().equals(contrasenia))
                .orElse(null);
    }

    /**
     * Crea o actualiza el UserEntity asociado al admin para que /api/auth/login
     * pueda autenticarlo. Sin este paso el admin existe en la tabla admin pero
     * nunca llega a user_entity y el login devuelve 400.
     */
    private void sincronizarUsuarioAdmin(Admin admin, String contraseniaPlana) {
        userRepository.findByCorreo(admin.getCorreo()).ifPresentOrElse(user -> {
            user.setNombre(admin.getNombre());
            user.setAdmin(admin);
            user.setRol(UserEntity.RolUsuario.ADMIN);
            user.setActivo(true);
            if (contraseniaPlana != null && !contraseniaPlana.isBlank()) {
                user.setContrasenia(passwordEncoder.encode(contraseniaPlana));
            }
            userRepository.save(user);
        }, () -> {
            String hash = (contraseniaPlana != null && !contraseniaPlana.isBlank())
                    ? passwordEncoder.encode(contraseniaPlana)
                    : passwordEncoder.encode(admin.getContrasenia());
            userRepository.save(UserEntity.deAdmin(admin, hash));
        });
    }
}
