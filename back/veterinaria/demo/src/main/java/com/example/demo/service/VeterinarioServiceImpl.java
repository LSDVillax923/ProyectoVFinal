package com.example.demo.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.entities.Veterinario;
import com.example.demo.errors.VeterinarioException;
import com.example.demo.repository.VeterinarioRepository;

@Service
@Transactional
public class VeterinarioServiceImpl implements VeterinarioService {

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Override
    public Veterinario findById(Long id) {
        return veterinarioRepository.findById(id)
                .orElseThrow(() -> new VeterinarioException("Veterinario no encontrado con ID: " + id));
    }

    @Override
    public List<Veterinario> findAll() {
        return veterinarioRepository.findAll();
    }

    @Override
    public List<Veterinario> findActivos() {
        return veterinarioRepository.findByEstado("activo");
    }

    @Override
    public Veterinario save(Veterinario veterinario) {
        validarVeterinario(veterinario);
        if (veterinario.getId() == null) {
            if (veterinarioRepository.existsByCorreo(veterinario.getCorreo())) {
                throw new IllegalArgumentException("Correo ya registrado");
            }
            if (veterinario.getCedula() != null && veterinarioRepository.existsByCedula(veterinario.getCedula())) {
                throw new IllegalArgumentException("Cédula ya registrada");
            }
        }
        if (veterinario.getEstado() == null) {
            veterinario.setEstado("activo");
        }
        return veterinarioRepository.save(veterinario);
    }

    @Override
    public Veterinario update(Long id, Veterinario veterinarioDetails) {
        Veterinario existing = findById(id);
        existing.setNombre(veterinarioDetails.getNombre());
        existing.setCedula(veterinarioDetails.getCedula());
        existing.setCelular(veterinarioDetails.getCelular());
        existing.setCorreo(veterinarioDetails.getCorreo());
        existing.setEspecialidad(veterinarioDetails.getEspecialidad());
        existing.setContrasenia(veterinarioDetails.getContrasenia());
        existing.setImageUrl(veterinarioDetails.getImageUrl());
        // estado y numAtenciones se actualizan con métodos específicos
        return veterinarioRepository.save(existing);
    }

    @Override
    public void cambiarEstado(Long id, String nuevoEstado) {
        Veterinario vet = findById(id);
        vet.setEstado(nuevoEstado);
        veterinarioRepository.save(vet);
    }

    @Override
    public void delete(Long id) {
        Veterinario vet = findById(id);
        veterinarioRepository.delete(vet);
    }

    @Override
    public Veterinario login(String correo, String contrasenia) {
        return veterinarioRepository.findByCorreo(correo)
                .filter(v -> v.getContrasenia().equals(contrasenia))
                .orElse(null);
    }

    @Override
    public void incrementarAtenciones(Long id) {
        Veterinario vet = findById(id);
        vet.setNumAtenciones(vet.getNumAtenciones() + 1);
        veterinarioRepository.save(vet);
    }

    private void validarVeterinario(Veterinario v) {
        if (v.getNombre() == null || v.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (v.getCorreo() == null || !v.getCorreo().contains("@")) {
            throw new IllegalArgumentException("Correo inválido");
        }
        if (v.getContrasenia() == null || v.getContrasenia().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
    }
}