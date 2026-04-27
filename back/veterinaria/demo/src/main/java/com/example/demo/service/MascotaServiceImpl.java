package com.example.demo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entities.Cliente;
import com.example.demo.entities.Mascota;
import com.example.demo.errors.MascotaException;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.MascotaRepository;
import com.example.demo.util.FechaUtils;

@Service
@Transactional
public class MascotaServiceImpl implements MascotaService {

    private static final Set<String> EXTENSIONES_PERMITIDAS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Value("${app.uploads.mascotas-dir:src/main/resources/static/img}")
    private String mascotasDir;

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
            mascota.setFoto("default-pet.png");
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

    @Override
    public Mascota subirFoto(Long id, MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo de la foto es obligatorio");
        }

        Mascota mascota = findById(id);

        String nombreOriginal = archivo.getOriginalFilename();
        if (nombreOriginal == null || !nombreOriginal.contains(".")) {
            throw new IllegalArgumentException("El archivo no tiene una extensión válida");
        }

        String extension = nombreOriginal
                .substring(nombreOriginal.lastIndexOf('.') + 1)
                .toLowerCase();

        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            throw new IllegalArgumentException(
                    "Extensión no permitida. Use: " + EXTENSIONES_PERMITIDAS);
        }

        try {
            Path directorio = Paths.get(mascotasDir).toAbsolutePath().normalize();
            Files.createDirectories(directorio);

            String nombreFoto = "pet" + id + "." + extension;

            for (String ext : EXTENSIONES_PERMITIDAS) {
                Path anterior = directorio.resolve("pet" + id + "." + ext);
                if (!ext.equals(extension)) {
                    Files.deleteIfExists(anterior);
                }
            }

            Path destino = directorio.resolve(nombreFoto);
            Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            mascota.setFoto(nombreFoto);
            return mascotaRepository.save(mascota);
        } catch (IOException e) {
            throw new MascotaException("No se pudo guardar la foto: " + e.getMessage());
        }
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