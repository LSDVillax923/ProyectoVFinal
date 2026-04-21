package com.example.demo.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.entities.Admin;
import com.example.demo.errors.AdminException;
import com.example.demo.repository.AdminRepository;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public Admin findById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new AdminException("Admin no encontrado con ID: " + id));
    }

    @Override
    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    @Override
    public Admin save(Admin admin) {
        // Validaciones de negocio
        if (admin.getNombre() == null || admin.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del administrador es obligatorio");
        }
        if (admin.getCorreo() == null || !admin.getCorreo().contains("@")) {
            throw new IllegalArgumentException("El correo debe ser válido");
        }
        if (admin.getId() == null && adminRepository.existsByCorreo(admin.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un admin con ese correo");
        }
        return adminRepository.save(admin);
    }

    @Override
    public Admin update(Long id, Admin adminDetails) {
        Admin existing = findById(id);
        existing.setNombre(adminDetails.getNombre());
        existing.setCorreo(adminDetails.getCorreo());
        existing.setContrasenia(adminDetails.getContrasenia());
        return adminRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Admin admin = findById(id);
        adminRepository.delete(admin);
    }

    @Override
    public Admin login(String correo, String contrasenia) {
        return adminRepository.findByCorreo(correo)
                .filter(a -> a.getContrasenia().equals(contrasenia))
                .orElse(null);
    }
}