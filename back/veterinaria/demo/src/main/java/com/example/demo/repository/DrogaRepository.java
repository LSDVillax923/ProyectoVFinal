package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Droga;

@Repository
public interface DrogaRepository extends JpaRepository<Droga, Long> {

    // Buscar droga por nombre
    Optional<Droga> findByNombre(String nombre);

    // Listar drogas con stock mayor a una cantidad específica
    List<Droga> findByUnidadesDisponiblesGreaterThan(int cantidad);

    // Descontar stock y aumentar unidades vendidas si hay disponibilidad
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