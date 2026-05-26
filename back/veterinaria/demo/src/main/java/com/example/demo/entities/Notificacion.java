package com.example.demo.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Notificación dirigida a un cliente. Se inserta en tres flujos:
 *  - Aprobación de una cita por un admin     (tipo = APROBADA)
 *  - Rechazo de una cita por un admin        (tipo = RECHAZADA)
 *  - Job diario que recuerda la cita del día siguiente (tipo = RECORDATORIO)
 */
@Entity
@Data
@NoArgsConstructor
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnoreProperties({"mascotas", "citas", "hibernateLazyInitializer", "handler"})
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNotificacion tipo;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Column(nullable = false)
    private boolean leida = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_id")
    @JsonIgnoreProperties({"cliente", "mascota", "veterinario", "hibernateLazyInitializer", "handler"})
    private Cita cita;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Notificacion(Cliente cliente, TipoNotificacion tipo, String mensaje, Cita cita) {
        this.cliente = cliente;
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.cita = cita;
        this.leida = false;
        this.createdAt = LocalDateTime.now();
    }

    public enum TipoNotificacion {
        APROBADA, RECHAZADA, RECORDATORIO, ASIGNADA
    }
}
