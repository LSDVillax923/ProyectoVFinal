package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.Cita;
import com.example.demo.entities.Notificacion.TipoNotificacion;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.NotificacionRepository;

/**
 * Job que se ejecuta una vez al día a las 08:00 (zona América/Bogotá) e
 * inserta una Notificacion RECORDATORIO para cada cita CONFIRMADA del día
 * siguiente. Evita duplicar si ya existe.
 */
@Component
public class RecordatorioCitaScheduler {

    @Autowired private CitaRepository citaRepository;
    @Autowired private NotificacionRepository notificacionRepository;
    @Autowired private NotificacionService notificacionService;

    @Scheduled(cron = "0 0 8 * * *", zone = "America/Bogota")
    @Transactional
    public void generarRecordatoriosDelDiaSiguiente() {
        LocalDate manana = LocalDate.now().plusDays(1);
        LocalDateTime desde = manana.atStartOfDay();
        LocalDateTime hasta = manana.atTime(23, 59, 59);

        List<Cita> citasManana = citaRepository
                .findByFechaInicioBetweenAndEstadoInOrderByFechaInicioAsc(
                        desde, hasta, List.of(Cita.EstadoCita.CONFIRMADA));

        for (Cita c : citasManana) {
            if (c.getCliente() == null) continue;
            if (notificacionRepository.existsByCita_IdAndTipo(c.getId(), TipoNotificacion.RECORDATORIO)) {
                continue;
            }
            notificacionService.notificarRecordatorio(c.getCliente(), c);
        }
    }
}
