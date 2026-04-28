package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Buscar cliente por correo
    Optional<Cliente> findByCorreo(String correo);

    // Buscar clientes por coincidencia en el nombre (sin distinguir mayúsculas/minúsculas)
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);

    // Buscar por nombre o apellido
    List<Cliente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
        String nombre, String apellido
    );

    // Búsqueda general por varios campos (nombre, apellido, correo, celular)
    @Query("SELECT c FROM Cliente c WHERE " +
           "(:query IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.apellido) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.correo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.celular) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Cliente> buscarPorFiltros(@Param("query") String query);

    // Verificar si existe un cliente con ese correo
    boolean existsByCorreo(String correo);
}