package com.example.demo.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.entities.Mascota;
import com.example.demo.entities.Tratamiento;
import com.example.demo.entities.Veterinario;
import com.example.demo.errors.TratamientoException;
import com.example.demo.repository.MascotaRepository;
import com.example.demo.repository.TratamientoRepository;
import com.example.demo.repository.VeterinarioRepository;

@Service
@Transactional
public class TratamientoServiceImpl implements TratamientoService {

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Override
    public Tratamiento findById(Long id) {
        return tratamientoRepository.findById(id)
                .orElseThrow(() -> new TratamientoException("Tratamiento no encontrado con ID: " + id));
    }

    @Override
    public List<Tratamiento> findAll() {
        return tratamientoRepository.findAll();
    }

    @Override
    public List<Tratamiento> findByMascotaId(Long mascotaId) {
        return tratamientoRepository.findByMascotaId(mascotaId);
    }

    @Override
    public List<Tratamiento> findByVeterinarioId(Long veterinarioId) {
        return tratamientoRepository.findByVeterinarioId(veterinarioId);
    }

    @Override
    public Tratamiento save(Tratamiento tratamiento, Long mascotaId, Long veterinarioId) {
        Mascota mascota = mascotaRepository.findById(mascotaId)
                .orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada"));
        Veterinario veterinario = veterinarioRepository.findById(veterinarioId)
                .orElseThrow(() -> new IllegalArgumentException("Veterinario no encontrado"));

                if (mascota.getEstado() != Mascota.EstadoMascota.ACTIVA) {
            throw new IllegalArgumentException("No se puede asignar tratamiento a una mascota inactiva");
        }

        tratamiento.setMascota(mascota);
        tratamiento.setVeterinario(veterinario);
        if (tratamiento.getEstado() == null) {
            tratamiento.setEstado(Tratamiento.EstadoTratamiento.PENDIENTE);
        }
        validarTratamiento(tratamiento);
        return tratamientoRepository.save(tratamiento);
    }

    @Override
    public Tratamiento update(Long id, Tratamiento tratamientoDetails) {
        Tratamiento existing = findById(id);
        existing.setDiagnostico(tratamientoDetails.getDiagnostico());
        existing.setObservaciones(tratamientoDetails.getObservaciones());
        existing.setFecha(tratamientoDetails.getFecha());
        existing.setEstado(tratamientoDetails.getEstado());
        // No se cambia mascota ni veterinario por este método (se podría agregar otro método específico)
        return tratamientoRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Tratamiento t = findById(id);
        tratamientoRepository.delete(t);
    }

    @Override
    public List<Tratamiento> findProgramados() {
        return tratamientoRepository.findTratamientosProgramados();
    }

    private void validarTratamiento(Tratamiento t) {
        if (t.getFecha() == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        if (t.getDiagnostico() == null || t.getDiagnostico().isBlank()) {
            throw new IllegalArgumentException("El diagnóstico es obligatorio");
        }
    }
}