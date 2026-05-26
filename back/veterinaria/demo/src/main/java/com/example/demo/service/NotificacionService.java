package com.example.demo.service;

import java.util.List;

import com.example.demo.entities.Cita;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.Notificacion;

public interface NotificacionService {

    /** Lista las notificaciones del cliente, más recientes primero. */
    List<Notificacion> findByCliente(Long clienteId);

    /** Cantidad de notificaciones aún no leídas del cliente. */
    long contarNoLeidas(Long clienteId);

    /** Marca una notificación como leída. */
    Notificacion marcarLeida(Long id);

    /** Crear y guardar una notificación cuando un admin asigna una cita directa al cliente. */
    Notificacion notificarAsignacion(Cliente cliente, Cita cita);

    /** Crear y guardar una notificación de aprobación de cita. */
    Notificacion notificarAprobacion(Cliente cliente, Cita cita);

    /** Crear y guardar una notificación de rechazo de cita con motivo opcional. */
    Notificacion notificarRechazo(Cliente cliente, Cita cita, String motivo);

    /** Crear y guardar una notificación de recordatorio (cita un día antes). */
    Notificacion notificarRecordatorio(Cliente cliente, Cita cita);
}
