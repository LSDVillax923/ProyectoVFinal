package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.Cliente;
import com.example.demo.entities.Mascota;
import com.example.demo.errors.MascotaException;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.MascotaRepository;
import com.example.demo.util.FechaUtils;

@Service
@Transactional
public class MascotaServiceImpl implements MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public Mascota findById(Long id) {
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new MascotaException("Mascota no encontrada con ID: " + id));
    }

    @Override
    public List<Mascota> findAll() {
        return mascotaRepository.findAll();
    }

    @Override
    public List<Mascota> findByClienteId(Long clienteId) {
        return mascotaRepository.findByCliente_Id(clienteId);
    }

    @Override
    public Mascota save(Mascota mascota, Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + clienteId));
        
        mascota.setCliente(cliente);
        
        // ✅ BACKEND calcula la edad automáticamente
        if (mascota.getFechaNacimiento() != null) {
            if (!FechaUtils.esFechaNacimientoValida(mascota.getFechaNacimiento())) {
                throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
            }
            mascota.setEdad(FechaUtils.calcularEdad(mascota.getFechaNacimiento()));
        } else {
            mascota.setEdad(0);
        }
        
        if (mascota.getEstado() == null) {
            mascota.setEstado(Mascota.EstadoMascota.ACTIVA);
        }
        if (mascota.getFoto() == null || mascota.getFoto().isBlank()) {
            mascota.setFoto("default.jpg");
        }
        
        validarMascota(mascota);
        return mascotaRepository.save(mascota);
    }

    @Override
    public Mascota update(Long id, Mascota mascotaDetails) {
        Mascota existing = findById(id);
        
        existing.setNombre(mascotaDetails.getNombre());
        existing.setEspecie(mascotaDetails.getEspecie());
        existing.setRaza(mascotaDetails.getRaza());
        existing.setSexo(mascotaDetails.getSexo());
        existing.setFechaNacimiento(mascotaDetails.getFechaNacimiento());
        
        // ✅ BACKEND recalcula la edad automáticamente
        if (mascotaDetails.getFechaNacimiento() != null) {
            if (!FechaUtils.esFechaNacimientoValida(mascotaDetails.getFechaNacimiento())) {
                throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
            }
            existing.setEdad(FechaUtils.calcularEdad(mascotaDetails.getFechaNacimiento()));
        } else {
            existing.setEdad(0);
        }
        
        existing.setPeso(mascotaDetails.getPeso());
        existing.setEnfermedad(mascotaDetails.getEnfermedad());
        existing.setObservaciones(mascotaDetails.getObservaciones());
        existing.setVeterinarioAsignado(mascotaDetails.getVeterinarioAsignado());
        
        if (mascotaDetails.getEstado() != null) {
            existing.setEstado(mascotaDetails.getEstado());
        }
        
        // La foto no se actualiza por este método (se podría agregar un endpoint específico)
        
        return mascotaRepository.save(existing);
    }

    @Override
    public Mascota patch(Long id, Mascota mascotaDetails) {
        Mascota existing = findById(id);

        if (mascotaDetails.getEstado() != null) {
            existing.setEstado(mascotaDetails.getEstado());
        }

        return mascotaRepository.save(existing);
    }


    @Override
    public void deactivate(Long id) {
        Mascota mascota = findById(id);
        mascota.setEstado(Mascota.EstadoMascota.INACTIVA);
        mascotaRepository.save(mascota);
    }

    @Override
    public void delete(Long id) {
        Mascota mascota = findById(id);
        mascotaRepository.delete(mascota);
    }

    @Override
    public List<Mascota> buscarPorFiltros(String query, String estado) {
        return mascotaRepository.buscarPorFiltros(query, estado);
    }

    @Override
    public long countByEstado(List<Mascota> mascotas, String estado) {
        return mascotas.stream()
                .filter(m -> m.getEstado().name().equalsIgnoreCase(estado))
                .count();
    }

    private void validarMascota(Mascota m) {
        if (m.getNombre() == null || m.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (m.getEspecie() == null || m.getEspecie().isBlank()) {
            throw new IllegalArgumentException("La especie es obligatoria");
        }
        if (m.getRaza() == null || m.getRaza().isBlank()) {
            throw new IllegalArgumentException("La raza es obligatoria");
        }
        if (m.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria");
        }
        if (m.getPeso() <= 0) {
            throw new IllegalArgumentException("Peso debe ser positivo");
        }
    }
}