package com.example.demo.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.Notificacion;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    /** Listar las notificaciones de un cliente, más recientes primero. */
    List<Notificacion> findByCliente_IdOrderByCreatedAtDesc(Long clienteId);

    long countByCliente_IdAndLeidaFalse(Long clienteId);

    /** Usado por el job de recordatorios para evitar duplicar la misma notificación. */
    boolean existsByCita_IdAndTipo(Long citaId, Notificacion.TipoNotificacion tipo);

    /** Recordatorios pendientes: citas que arrancan dentro del rango y aún no tienen notificación RECORDATORIO. */
    List<Notificacion> findByTipoAndCreatedAtBetween(Notificacion.TipoNotificacion tipo,
                                                    LocalDateTime desde,
                                                    LocalDateTime hasta);
}
