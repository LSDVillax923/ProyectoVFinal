package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Veterinario;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    // Buscar veterinario por correo
    Optional<Veterinario> findByCorreo(String correo);

    // Buscar veterinario por cédula
    Optional<Veterinario> findByCedula(String cedula);

    // Verificar si existe un veterinario con ese correo
    boolean existsByCorreo(String correo);

    // Verificar si existe un veterinario con esa cédula
    boolean existsByCedula(String cedula);

    // Listar veterinarios por estado
    List<Veterinario> findByEstado(String estado);
    
    // KPI dashboard: cuenta veterinarios por estado ("activo" / "inactivo")
    long countByEstadoIgnoreCase(String estado);

}