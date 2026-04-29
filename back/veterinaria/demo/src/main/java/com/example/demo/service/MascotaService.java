package com.example.demo.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entities.Mascota;

/**
 * Servicio para gestionar mascotas.
 */
public interface MascotaService {

    /** Busca una mascota por ID */
    Mascota findById(Long id);

    /** Lista todas las mascotas */
    List<Mascota> findAll();

    /** Lista mascotas por cliente */
    List<Mascota> findByClienteId(Long clienteId);

    /** Guarda una mascota */
    Mascota save(Mascota mascota, Long clienteId);

    /** Actualiza una mascota */
    Mascota update(Long id, Mascota mascotaDetails);

    /** Actualiza parcialmente una mascota */
    Mascota patch(Long id, Mascota mascotaDetails);

    /** Desactiva una mascota */
    void deactivate(Long id);

    /** Elimina una mascota */
    void delete(Long id);

    /** Busca mascotas por filtros */
    List<Mascota> buscarPorFiltros(String query, String estado);

    /** Cuenta mascotas por estado */
    long countByEstado(List<Mascota> mascotas, String estado);

    /** Sube foto de la mascota */
    Mascota subirFoto(Long id, MultipartFile archivo);

    /** KPI dashboard: total de mascotas registradas */
    long contar();

    /** KPI dashboard: total de mascotas en un estado dado */
    long contarPorEstado(Mascota.EstadoMascota estado);
}