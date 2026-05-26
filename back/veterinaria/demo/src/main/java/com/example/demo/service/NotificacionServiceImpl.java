package com.example.demo.service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.Cita;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.Notificacion;
import com.example.demo.entities.Notificacion.TipoNotificacion;
import com.example.demo.repository.NotificacionRepository;

@Service
@Transactional
public class NotificacionServiceImpl implements NotificacionService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Override
    public List<Notificacion> findByCliente(Long clienteId) {
        return notificacionRepository.findByCliente_IdOrderByCreatedAtDesc(clienteId);
    }

    @Override
    public long contarNoLeidas(Long clienteId) {
        return notificacionRepository.countByCliente_IdAndLeidaFalse(clienteId);
    }

    @Override
    public Notificacion marcarLeida(Long id) {
        Notificacion n = notificacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada"));
        n.setLeida(true);
        return notificacionRepository.save(n);
    }

    @Override
    public Notificacion notificarAsignacion(Cliente cliente, Cita cita) {
        String fecha = cita.getFechaInicio() != null ? cita.getFechaInicio().format(FMT) : "";
        String vet = cita.getVeterinario() != null ? cita.getVeterinario().getNombre() : "el veterinario asignado";
        String mensaje = "Se te asignó una cita el " + fecha + " con " + vet + ".";
        return notificacionRepository.save(new Notificacion(cliente, TipoNotificacion.ASIGNADA, mensaje, cita));
    }

    @Override
    public Notificacion notificarAprobacion(Cliente cliente, Cita cita) {
        String fecha = cita.getFechaInicio() != null ? cita.getFechaInicio().format(FMT) : "";
        String mensaje = "Tu cita del " + fecha + " fue aprobada.";
        return notificacionRepository.save(new Notificacion(cliente, TipoNotificacion.APROBADA, mensaje, cita));
    }

    @Override
    public Notificacion notificarRechazo(Cliente cliente, Cita cita, String motivo) {
        String fecha = cita.getFechaInicio() != null ? cita.getFechaInicio().format(FMT) : "";
        String mensaje = "Tu cita del " + fecha + " fue rechazada"
                + (motivo != null && !motivo.isBlank() ? ": " + motivo : ".");
        return notificacionRepository.save(new Notificacion(cliente, TipoNotificacion.RECHAZADA, mensaje, cita));
    }

    @Override
    public Notificacion notificarRecordatorio(Cliente cliente, Cita cita) {
        String fecha = cita.getFechaInicio() != null ? cita.getFechaInicio().format(FMT) : "";
        String mensaje = "Recordatorio: tienes una cita mañana " + fecha + ".";
        return notificacionRepository.save(new Notificacion(cliente, TipoNotificacion.RECORDATORIO, mensaje, cita));
    }
}
