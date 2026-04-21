package com.example.demo.service;

import java.util.List;
import com.example.demo.entities.Mascota;

public interface MascotaService {
    Mascota findById(Long id);
    List<Mascota> findAll();
    List<Mascota> findByClienteId(Long clienteId);
    Mascota save(Mascota mascota, Long clienteId);
    Mascota update(Long id, Mascota mascotaDetails);
    void deactivate(Long id);
    void delete(Long id);
    List<Mascota> buscarPorFiltros(String query, String estado);
    long countByEstado(List<Mascota> mascotas, String estado);
}