package com.example.demo.service;

import java.util.List;
import com.example.demo.entities.Veterinario;

/**
 * Servicio para gestionar veterinarios.
 */
public interface VeterinarioService {

    /** Busca un veterinario por ID */
    Veterinario findById(Long id);

    /** Lista todos los veterinarios */
    List<Veterinario> findAll();

    /** Lista veterinarios activos */
    List<Veterinario> findActivos();

    /** Guarda un veterinario */
    Veterinario save(Veterinario veterinario);

    /** Actualiza un veterinario */
    Veterinario update(Long id, Veterinario veterinarioDetails);

    /** Cambia el estado del veterinario */
    void cambiarEstado(Long id, String nuevoEstado);

    /** Elimina un veterinario */
    void delete(Long id);

    /** Login de veterinario */
    Veterinario login(String correo, String contrasenia);

    /** Incrementa número de atenciones */
    void incrementarAtenciones(Long id);

    /** KPI dashboard: cuenta veterinarios por estado ("activo" / "inactivo") */
    long contarPorEstado(String estado);
}