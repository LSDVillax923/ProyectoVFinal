package com.example.demo.service;

import java.util.List;

import com.example.demo.entities.Admin;

/**
 * Servicio para gestionar administradores.
 */
public interface AdminService {

    /** Busca un admin por ID */
    Admin findById(Long id);

    /** Lista todos los admins */
    List<Admin> findAll();

    /** Guarda un admin */
    Admin save(Admin admin);

    /** Actualiza un admin */
    Admin update(Long id, Admin adminDetails);

    /** Elimina un admin */
    void delete(Long id);

    /** Login de admin */
    Admin login(String correo, String contrasenia);
}