package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Veterinario;

@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    Optional<Veterinario> findByCorreo(String correo);

    Optional<Veterinario> findByCedula(String cedula);

    boolean existsByCorreo(String correo);

    boolean existsByCedula(String cedula);

    List<Veterinario> findByEstado(String estado);

    // KPI dashboard: cuenta veterinarios por estado ("activo" / "inactivo")
    long countByEstadoIgnoreCase(String estado);
}
