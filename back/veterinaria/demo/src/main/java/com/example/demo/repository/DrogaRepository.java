package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Droga;

@Repository
public interface DrogaRepository extends JpaRepository<Droga, Long> {

    Optional<Droga> findByNombre(String nombre);

    List<Droga> findByUnidadesDisponiblesGreaterThan(int cantidad);
}

