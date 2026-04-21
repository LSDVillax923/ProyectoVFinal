package com.example.demo.service;

import java.util.List;
import com.example.demo.entities.Veterinario;

public interface VeterinarioService {
    Veterinario findById(Long id);
    List<Veterinario> findAll();
    List<Veterinario> findActivos();
    Veterinario save(Veterinario veterinario);
    Veterinario update(Long id, Veterinario veterinarioDetails);
    void cambiarEstado(Long id, String nuevoEstado);
    void delete(Long id);
    Veterinario login(String correo, String contrasenia);
    void incrementarAtenciones(Long id);
}