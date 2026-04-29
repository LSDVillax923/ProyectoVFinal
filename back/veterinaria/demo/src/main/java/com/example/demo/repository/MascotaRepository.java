package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Mascota;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    // Buscar mascotas por ID del cliente
    List<Mascota> findByCliente_Id(Long clienteId);

    // Buscar mascotas por estado
    List<Mascota> findByEstado(String estado);

    // Búsqueda con filtros por nombre, raza, cliente y estado
    @Query("SELECT m FROM Mascota m WHERE " +
           "(:query IS NULL OR LOWER(m.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.raza) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.cliente.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.cliente.apellido) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:estado IS NULL OR LOWER(m.estado) = LOWER(:estado))")
    List<Mascota> buscarPorFiltros(@Param("query") String query, @Param("estado") String estado);

    // KPI dashboard: cuenta mascotas por estado (TRATAMIENTO, ACTIVA, INACTIVA)
    long countByEstado(Mascota.EstadoMascota estado);
}