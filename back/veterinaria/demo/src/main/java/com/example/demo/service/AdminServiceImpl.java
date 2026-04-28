package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.Admin;
import com.example.demo.errors.AdminException;
import com.example.demo.repository.AdminRepository;

/**
 * Implementación del servicio de administradores.
 */
@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

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
        if (admin.getId() == null && adminRepository.existsByCorreo(admin.getCorreo())) {
            throw new IllegalArgumentException("Correo ya registrado");
        }
        return adminRepository.save(admin);
    }

    /** Actualiza un admin */
    @Override
    public Admin update(Long id, Admin adminDetails) {
        Admin existing = findById(id);
        existing.setNombre(adminDetails.getNombre());
        existing.setCorreo(adminDetails.getCorreo());
        existing.setContrasenia(adminDetails.getContrasenia());
        return adminRepository.save(existing);
    }

    /** Elimina un admin */
    @Override
    public void delete(Long id) {
        Admin admin = findById(id);
        adminRepository.delete(admin);
    }

    /** Login de admin */
    @Override
    public Admin login(String correo, String contrasenia) {
        return adminRepository.findByCorreo(correo)
                .filter(a -> a.getContrasenia().equals(contrasenia))
                .orElse(null);
    }
}