package com.example.demo.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.CitaResumenDto;
import com.example.demo.dto.SlotDisponibleDto;
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

    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // Horario laboral: L-V 8:00 – 18:00, slots de 30 minutos
    private static final LocalTime JORNADA_INICIO = LocalTime.of(8, 0);
    private static final LocalTime JORNADA_FIN    = LocalTime.of(18, 0);
    private static final int SLOT_MINUTOS = 30;

    @Autowired private CitaRepository citaRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private MascotaRepository mascotaRepository;
    @Autowired private VeterinarioRepository veterinarioRepository;

    @Override public Cita findById(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new CitaException("Cita no encontrada con ID: " + id));
    }

    @Override public List<Cita> findAll() { return citaRepository.findAll(); }
    @Override public List<Cita> findByVeterinarioId(Long veterinarioId) { return citaRepository.findByVeterinarioId(veterinarioId); }
    @Override public List<Cita> findByMascotaId(Long mascotaId) { return citaRepository.findByMascotaId(mascotaId); }
    @Override public List<Cita> findByClienteId(Long clienteId) { return citaRepository.findByClienteId(clienteId); }

    @Override
    public Cita save(Cita cita, Long clienteId, Long mascotaId, Long veterinarioId) {
        Cita guardada = persistirCita(cita, clienteId, mascotaId, veterinarioId);
        if (guardada.getEstado() == null) {
            guardada.setEstado(Cita.EstadoCita.CONFIRMADA);
            return citaRepository.save(guardada);
        }
        return guardada;
    }

    @Override
    public Cita solicitar(Cita cita, Long clienteId, Long mascotaId, Long veterinarioId) {
        cita.setEstado(Cita.EstadoCita.PENDIENTE);
        cita.setMotivoRechazo(null);
        return persistirCita(cita, clienteId, mascotaId, veterinarioId);
    }

    @Override
    public Cita aprobar(Long id) {
        Cita cita = findById(id);
        if (cita.getEstado() != Cita.EstadoCita.PENDIENTE) {
            throw new IllegalArgumentException("Solo se pueden aprobar citas en estado PENDIENTE");
        }
        cita.setEstado(Cita.EstadoCita.CONFIRMADA);
        cita.setMotivoRechazo(null);
        return citaRepository.save(cita);
    }

    @Override
    public Cita rechazar(Long id, String motivo) {
        Cita cita = findById(id);
        if (cita.getEstado() != Cita.EstadoCita.PENDIENTE) {
            throw new IllegalArgumentException("Solo se pueden rechazar citas en estado PENDIENTE");
        }
        cita.setEstado(Cita.EstadoCita.CANCELADA);
        cita.setMotivoRechazo(motivo);
        return citaRepository.save(cita);
    }

    private Cita persistirCita(Cita cita, Long clienteId, Long mascotaId, Long veterinarioId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        Mascota mascota = mascotaRepository.findById(mascotaId)
                .orElseThrow(() -> new IllegalArgumentException("Mascota no encontrada"));
        Veterinario veterinario = veterinarioRepository.findById(veterinarioId)
                .orElseThrow(() -> new IllegalArgumentException("Veterinario no encontrado"));

        if (!mascota.getCliente().getId().equals(clienteId)) {
            throw new IllegalArgumentException("La mascota no pertenece al cliente indicado");
        }
        if (mascota.getEstado() == Mascota.EstadoMascota.INACTIVA) {
            throw new IllegalArgumentException("No se puede asignar una cita a una mascota inactiva");
        }
        if (veterinario.getEstado() != null && !"activo".equalsIgnoreCase(veterinario.getEstado())) {
            throw new IllegalArgumentException("El veterinario seleccionado no está activo");
        }

        if (cita.getFechaInicio() == null || cita.getFechaFin() == null) {
            throw new IllegalArgumentException("Fechas de inicio y fin son obligatorias");
        }
        if (cita.getFechaInicio().isAfter(cita.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la de fin");
        }
        LocalDateTime ahora = LocalDateTime.now();
        if (cita.getFechaInicio().toLocalDate().isBefore(ahora.toLocalDate())) {
            throw new IllegalArgumentException("No se pueden agendar citas en fechas anteriores a hoy");
        }
        if (cita.getFechaInicio().isBefore(ahora)) {
            throw new IllegalArgumentException("La hora de la cita ya pasó. Selecciona un horario futuro");
        }
        DayOfWeek dia = cita.getFechaInicio().getDayOfWeek();
        if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("Este día es inválido: la veterinaria no atiende los fines de semana");
        }

        List<Cita> citasSolapadas = citaRepository.findCitasSolapadas(
                veterinarioId, cita.getFechaInicio(), cita.getFechaFin());
        boolean conflicto = citasSolapadas.stream().anyMatch(c -> c.getEstado() != Cita.EstadoCita.CANCELADA);
        if (conflicto) {
            throw new IllegalArgumentException("El veterinario ya tiene una cita en ese horario");
        }

        cita.setCliente(cliente);
        cita.setMascota(mascota);
        cita.setVeterinario(veterinario);
        return citaRepository.save(cita);
    }

    @Override
    public Cita update(Long id, Cita citaDetails) {
        Cita existing = findById(id);
        if (citaDetails.getMotivo() != null) existing.setMotivo(citaDetails.getMotivo());

        if (citaDetails.getFechaInicio() != null && citaDetails.getFechaFin() != null) {
            if (citaDetails.getFechaInicio().isAfter(citaDetails.getFechaFin())) {
                throw new IllegalArgumentException("Fecha inicio posterior a fin");
            }
            LocalDateTime ahora = LocalDateTime.now();
            if (citaDetails.getFechaInicio().toLocalDate().isBefore(ahora.toLocalDate())) {
                throw new IllegalArgumentException("No se pueden agendar citas en fechas anteriores a hoy");
            }
            if (citaDetails.getFechaInicio().isBefore(ahora)) {
                throw new IllegalArgumentException("La hora de la cita ya pasó. Selecciona un horario futuro");
            }
            DayOfWeek dia = citaDetails.getFechaInicio().getDayOfWeek();
            if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY) {
                throw new IllegalArgumentException("Este día es inválido: la veterinaria no atiende los fines de semana");
            }
            List<Cita> solapadas = citaRepository.findCitasSolapadas(
                    existing.getVeterinario().getId(), citaDetails.getFechaInicio(), citaDetails.getFechaFin());
            boolean conflicto = solapadas.stream()
                    .filter(c -> !c.getId().equals(id))
                    .anyMatch(c -> c.getEstado() != Cita.EstadoCita.CANCELADA);
            if (conflicto) throw new IllegalArgumentException("Horario no disponible");
            existing.setFechaInicio(citaDetails.getFechaInicio());
            existing.setFechaFin(citaDetails.getFechaFin());
        }
        if (citaDetails.getEstado() != null) existing.setEstado(citaDetails.getEstado());
        return citaRepository.save(existing);
    }

    @Override
    public void cancelar(Long id) {
        Cita cita = findById(id);
        cita.setEstado(Cita.EstadoCita.CANCELADA);
        citaRepository.save(cita);
    }

    @Override public void delete(Long id) { citaRepository.delete(findById(id)); }

    @Override
    public List<Cita> findCitasEnRango(LocalDateTime inicio, LocalDateTime fin) {
        return citaRepository.findAll().stream()
                .filter(c -> !c.getFechaFin().isBefore(inicio) && !c.getFechaInicio().isAfter(fin))
                .toList();
    }

    @Override
    public long contarPorRango(LocalDateTime inicio, LocalDateTime fin) {
        return citaRepository.countByFechaInicioBetween(inicio, fin);
    }

    @Override
    public List<CitaResumenDto> proximasEnRango(LocalDateTime inicio, LocalDateTime fin, int limite) {
        int n = limite > 0 ? limite : 5;
        List<Cita.EstadoCita> estados = List.of(Cita.EstadoCita.PENDIENTE, Cita.EstadoCita.CONFIRMADA);
        return citaRepository
                .findByFechaInicioBetweenAndEstadoInOrderByFechaInicioAsc(inicio, fin, estados)
                .stream()
                .limit(n)
                .map(this::toResumen)
                .toList();
    }

    @Override
    public List<SlotDisponibleDto> disponibilidad(Long veterinarioId, LocalDate fecha) {
        Veterinario vet = veterinarioRepository.findById(veterinarioId)
                .orElseThrow(() -> new IllegalArgumentException("Veterinario no encontrado"));
        if (vet.getEstado() != null && !"activo".equalsIgnoreCase(vet.getEstado())) {
            return List.of();
        }
        // No se generan slots en sábado ni domingo.
        DayOfWeek dia = fecha.getDayOfWeek();
        if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY) {
            return List.of();
        }

        LocalDateTime inicioJornada = fecha.atTime(JORNADA_INICIO);
        LocalDateTime finJornada    = fecha.atTime(JORNADA_FIN);

        // Citas existentes del día (sin cancelar)
        List<Cita> ocupadas = citaRepository.findCitasSolapadas(veterinarioId, inicioJornada, finJornada)
                .stream()
                .filter(c -> c.getEstado() != Cita.EstadoCita.CANCELADA)
                .toList();

        LocalDateTime ahora = LocalDateTime.now();
        List<SlotDisponibleDto> slots = new ArrayList<>();
        for (LocalDateTime slot = inicioJornada; slot.isBefore(finJornada); slot = slot.plusMinutes(SLOT_MINUTOS)) {
            LocalDateTime slotFin = slot.plusMinutes(SLOT_MINUTOS);
            // No mostrar slots que ya pasaron
            if (slotFin.isBefore(ahora)) continue;
            final LocalDateTime s = slot;
            boolean ocupado = ocupadas.stream().anyMatch(c -> seSolapan(c.getFechaInicio(), c.getFechaFin(), s, slotFin));
            if (!ocupado) slots.add(new SlotDisponibleDto(slot, slotFin));
        }
        return slots;
    }

    private boolean seSolapan(LocalDateTime aIni, LocalDateTime aFin, LocalDateTime bIni, LocalDateTime bFin) {
        return aIni.isBefore(bFin) && bIni.isBefore(aFin);
    }

    private CitaResumenDto toResumen(Cita cita) {
        Mascota mascota = cita.getMascota();
        Cliente cliente = cita.getCliente();
        String hora = cita.getFechaInicio() != null ? cita.getFechaInicio().format(HORA_FORMATTER) : "";
        String nombreMascota = mascota != null ? mascota.getNombre() : "Sin mascota";
        String duenio = cliente != null
                ? (safe(cliente.getNombre()) + " " + safe(cliente.getApellido())).trim()
                : "";
        String estado = cita.getEstado() != null ? cita.getEstado().name() : "";
        return new CitaResumenDto(hora, nombreMascota, duenio, cita.getMotivo(), estado);
    }

    private String safe(String s) { return s == null ? "" : s; }
}
