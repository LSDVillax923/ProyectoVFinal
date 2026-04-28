package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    // Buscar un admin por su correo
    Optional<Admin> findByCorreo(String correo);

    // Verificar si existe un admin con ese correo
    boolean existsByCorreo(String correo);
}