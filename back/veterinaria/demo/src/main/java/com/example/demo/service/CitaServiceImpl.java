package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.Mascota;
import com.example.demo.entities.Veterinario;
import com.example.demo.entities.Cita;
import com.example.demo.errors.CitaException;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.MascotaRepository;
import com.example.demo.repository.VeterinarioRepository;
import com.example.demo.repository.CitaRepository;

@Service
@Transactional
public class CitaServiceImpl implements CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Override
    public Cita findById(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new CitaException("Cita no encontrada con ID: " + id));
    }

    @Override
    public List<Cita> findAll() {
        return citaRepository.findAll();
    }

    @Override
    public List<Cita> findByVeterinarioId(Long veterinarioId) {
        return citaRepository.findByVeterinarioId(veterinarioId);
    }

    @Override
    public List<Cita> findByMascotaId(Long mascotaId) {
        return citaRepository.findByMascotaId(mascotaId);
    }

    @Override
    public List<Cita> findByClienteId(Long clienteId) {
        return citaRepository.findByClienteId(clienteId);
    }

    @Override
    public Cita save(Cita cita, Long clienteId, Long mascotaId, Long veterinarioId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        Mascota mascota = mascotaRepository.findById(mascotaId)
                .orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada"));
        Veterinario veterinario = veterinarioRepository.findById(veterinarioId)
                .orElseThrow(() -> new IllegalArgumentException("Veterinario no encontrado"));

        // Validar que la mascota pertenezca al cliente
        if (!mascota.getCliente().getId().equals(clienteId)) {
            throw new IllegalArgumentException("La mascota no pertenece al cliente indicado");
        }

        // Validar fechas
        if (cita.getFechaInicio() == null || cita.getFechaFin() == null) {
            throw new IllegalArgumentException("Fechas de inicio y fin son obligatorias");
        }
        if (cita.getFechaInicio().isAfter(cita.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la de fin");
        }
        if (cita.getFechaInicio().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La cita debe programarse para una fecha futura");
        }

        // Validar disponibilidad del veterinario (sin solapamiento)
        List<Cita> citasSolapadas = citaRepository.findCitasSolapadas(
                veterinarioId, cita.getFechaInicio(), cita.getFechaFin());
        if (!citasSolapadas.isEmpty()) {
            throw new IllegalArgumentException("El veterinario ya tiene una cita en ese horario");
        }

        cita.setCliente(cliente);
        cita.setMascota(mascota);
        cita.setVeterinario(veterinario);
        if (cita.getEstado() == null) {
            cita.setEstado(Cita.EstadoCita.PENDIENTE);
        }
        return citaRepository.save(cita);
    }

    @Override
    public Cita update(Long id, Cita citaDetails) {
        Cita existing = findById(id);
        // Solo se permite modificar motivo, fechas y estado (con restricciones)
        if (citaDetails.getMotivo() != null) {
            existing.setMotivo(citaDetails.getMotivo());
        }
        // Cambio de fechas requiere volver a validar solapamiento (implementación simplificada)
        if (citaDetails.getFechaInicio() != null && citaDetails.getFechaFin() != null) {
            if (citaDetails.getFechaInicio().isAfter(citaDetails.getFechaFin())) {
                throw new IllegalArgumentException("Fecha inicio posterior a fin");
            }
            // Verificar disponibilidad (excepto la propia cita)
            List<Cita> solapadas = citaRepository.findCitasSolapadas(
                    existing.getVeterinario().getId(), citaDetails.getFechaInicio(), citaDetails.getFechaFin());
            boolean conflicto = solapadas.stream().anyMatch(c -> !c.getId().equals(id));
            if (conflicto) {
                throw new IllegalArgumentException("Horario no disponible");
            }
            existing.setFechaInicio(citaDetails.getFechaInicio());
            existing.setFechaFin(citaDetails.getFechaFin());
        }
        if (citaDetails.getEstado() != null) {
            existing.setEstado(citaDetails.getEstado());
        }
        return citaRepository.save(existing);
    }

    @Override
    public void cancelar(Long id) {
        Cita cita = findById(id);
        cita.setEstado(Cita.EstadoCita.CANCELADA);
        citaRepository.save(cita);
    }

    @Override
    public void delete(Long id) {
        Cita cita = findById(id);
        citaRepository.delete(cita);
    }

    @Override
    public List<Cita> findCitasEnRango(LocalDateTime inicio, LocalDateTime fin) {
        // Implementación básica usando el repositorio (puedes agregar método personalizado)
        return citaRepository.findAll().stream()
                .filter(c -> !c.getFechaFin().isBefore(inicio) && !c.getFechaInicio().isAfter(fin))
                .toList();
    }
}