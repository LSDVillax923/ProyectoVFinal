package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.DashboardMetricasDto;
import com.example.demo.dto.DashboardMetricasDto.CitaResumenDto;
import com.example.demo.dto.DashboardMetricasDto.MedicamentoCantidadDto;
import com.example.demo.entities.Cita;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.Droga;
import com.example.demo.entities.Mascota;
import com.example.demo.entities.Tratamiento;
import com.example.demo.entities.Veterinario;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.DrogaRepository;
import com.example.demo.repository.MascotaRepository;
import com.example.demo.repository.TratamientoRepository;
import com.example.demo.repository.VeterinarioRepository;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final int TOP_MEDICAMENTOS_LIMITE = 3;
    private static final int CITAS_PROXIMAS_LIMITE = 5;

    @Autowired private ClienteRepository clienteRepository;
    @Autowired private MascotaRepository mascotaRepository;
    @Autowired private VeterinarioRepository veterinarioRepository;
    @Autowired private TratamientoRepository tratamientoRepository;
    @Autowired private DrogaRepository drogaRepository;
    @Autowired private CitaRepository citaRepository;

    @Override
    public DashboardMetricasDto obtenerMetricas() {
        long totalClientes = clienteRepository.count();

        List<Mascota> mascotas = mascotaRepository.findAll();
        long totalMascotas = mascotas.size();
        long mascotasEnTratamiento = mascotas.stream()
                .filter(m -> m.getEstado() == Mascota.EstadoMascota.TRATAMIENTO)
                .count();

        List<Veterinario> veterinarios = veterinarioRepository.findAll();
        long veterinariosActivos = veterinarios.stream()
                .filter(v -> "activo".equalsIgnoreCase(v.getEstado()))
                .count();
        long veterinariosInactivos = veterinarios.stream()
                .filter(v -> "inactivo".equalsIgnoreCase(v.getEstado()))
                .count();

        List<Tratamiento> tratamientos = tratamientoRepository.findAll();
        LocalDate hoy = LocalDate.now();
        LocalDate haceUnMes = hoy.minusMonths(1);
        List<Tratamiento> tratamientosRecientes = tratamientos.stream()
                .filter(t -> t.getFecha() != null
                        && !t.getFecha().isBefore(haceUnMes)
                        && !t.getFecha().isAfter(hoy))
                .toList();

        List<MedicamentoCantidadDto> medicamentosUltimoMes =
                calcularMedicamentosUltimoMes(tratamientosRecientes);

        List<Droga> drogas = drogaRepository.findAll();
        double ventasTotales = drogas.stream()
                .mapToDouble(d -> d.getPrecioVenta() * d.getUnidadesVendidas())
                .sum();
        double gananciasTotales = drogas.stream()
                .mapToDouble(d -> (d.getPrecioVenta() - d.getPrecioCompra()) * d.getUnidadesVendidas())
                .sum();

        List<MedicamentoCantidadDto> topMedicamentos = drogas.stream()
                .filter(d -> d.getUnidadesVendidas() > 0)
                .sorted(Comparator.comparingInt(Droga::getUnidadesVendidas).reversed())
                .limit(TOP_MEDICAMENTOS_LIMITE)
                .map(d -> new MedicamentoCantidadDto(d.getNombre(), d.getUnidadesVendidas()))
                .toList();

        LocalDateTime inicioDia = hoy.atStartOfDay();
        LocalDateTime finDia = hoy.atTime(LocalTime.MAX);
        List<Cita> citasDelDia = citaRepository.findAll().stream()
                .filter(c -> c.getFechaInicio() != null
                        && !c.getFechaInicio().isBefore(inicioDia)
                        && !c.getFechaInicio().isAfter(finDia))
                .toList();

        long citasHoy = citasDelDia.size();
        List<CitaResumenDto> citasProximas = citasDelDia.stream()
                .filter(c -> c.getEstado() == Cita.EstadoCita.PENDIENTE
                        || c.getEstado() == Cita.EstadoCita.CONFIRMADA)
                .sorted(Comparator.comparing(Cita::getFechaInicio))
                .limit(CITAS_PROXIMAS_LIMITE)
                .map(this::toResumen)
                .toList();

        return new DashboardMetricasDto(
                totalClientes,
                totalMascotas,
                mascotasEnTratamiento,
                veterinariosActivos,
                veterinariosInactivos,
                tratamientosRecientes.size(),
                citasHoy,
                ventasTotales,
                gananciasTotales,
                topMedicamentos,
                medicamentosUltimoMes,
                citasProximas
        );
    }

    private List<MedicamentoCantidadDto> calcularMedicamentosUltimoMes(List<Tratamiento> tratamientos) {
        Map<String, Long> conteo = new HashMap<>();
        for (Tratamiento t : tratamientos) {
            if (t.getDrogas() == null) continue;
            t.getDrogas().forEach(td -> {
                String nombre = td.getDroga() != null && td.getDroga().getNombre() != null
                        ? td.getDroga().getNombre()
                        : "Sin nombre";
                long cantidad = td.getCantidad();
                conteo.merge(nombre, cantidad, Long::sum);
            });
        }
        return conteo.entrySet().stream()
                .map(e -> new MedicamentoCantidadDto(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(MedicamentoCantidadDto::cantidad).reversed())
                .toList();
    }

    private CitaResumenDto toResumen(Cita cita) {
        Mascota mascota = cita.getMascota();
        Cliente cliente = cita.getCliente();
        String nombreMascota = mascota != null ? mascota.getNombre() : "Sin mascota";
        String duenio = cliente != null
                ? (safe(cliente.getNombre()) + " " + safe(cliente.getApellido())).trim()
                : "";
        String hora = cita.getFechaInicio() != null
                ? cita.getFechaInicio().format(HORA_FORMATTER)
                : "";
        String estado = cita.getEstado() != null ? cita.getEstado().name() : "";
        return new CitaResumenDto(hora, nombreMascota, duenio, cita.getMotivo(), estado);
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
