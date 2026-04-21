package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Droga;

@Repository
public interface DrogaRepository extends JpaRepository<Droga, Long> {

    Optional<Droga> findByNombre(String nombre);

    List<Droga> findByUnidadesDisponiblesGreaterThan(int cantidad);
    @Modifying
    @Query("""
            UPDATE Droga d
            SET d.unidadesDisponibles = d.unidadesDisponibles - :cantidad,
                d.unidadesVendidas = d.unidadesVendidas + :cantidad
            WHERE d.id = :id
              AND d.unidadesDisponibles >= :cantidad
            """)
    int descontarStockSiDisponible(@Param("id") Long id, @Param("cantidad") int cantidad);
}