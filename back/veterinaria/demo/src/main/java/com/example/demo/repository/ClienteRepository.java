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
    Optional<Cliente> findByCorreo(String correo);

    List<Cliente> findByNombreContainingIgnoreCase(String nombre);


       List<Cliente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
        String nombre, String apellido);

    @Query("SELECT c FROM Cliente c WHERE " +
           "(:query IS NULL OR LOWER(c.nombre)   LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.apellido) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.correo)   LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.celular)  LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Cliente> buscarPorFiltros(@Param("query") String query);

    boolean existsByCorreo(String correo);
}
