package com.example.demo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.CitaDetalleDto;
import com.example.demo.dto.CitaResumenDto;
import com.example.demo.dto.DtoMapper;
import com.example.demo.dto.SlotDisponibleDto;
import com.example.demo.entities.Cita;
import com.example.demo.service.CitaService;
import com.example.demo.service.NotificacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private NotificacionService notificacionService;

    @GetMapping
    public ResponseEntity<List<CitaDetalleDto>> findAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<Cita> resultado = (inicio != null && fin != null)
                ? citaService.findCitasEnRango(inicio, fin)
                : citaService.findAll();
        return ResponseEntity.ok(DtoMapper.toCitaDetalleDtoList(resultado));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<CitaDetalleDto>> pendientes() {
        // Bandeja del admin: solo solicitudes en PENDIENTE.
        List<Cita> resultado = citaService.findAll().stream()
                .filter(c -> c.getEstado() == Cita.EstadoCita.PENDIENTE)
                .toList();
        return ResponseEntity.ok(DtoMapper.toCitaDetalleDtoList(resultado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaDetalleDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(DtoMapper.toCitaDetalleDto(citaService.findById(id)));
    }

    @GetMapping("/veterinario/{veterinarioId}")
    public ResponseEntity<List<CitaDetalleDto>> findByVeterinarioId(@PathVariable Long veterinarioId) {
        return ResponseEntity.ok(DtoMapper.toCitaDetalleDtoList(citaService.findByVeterinarioId(veterinarioId)));
    }

    @GetMapping("/mascota/{mascotaId}")
    public ResponseEntity<List<CitaDetalleDto>> findByMascotaId(@PathVariable Long mascotaId) {
        return ResponseEntity.ok(DtoMapper.toCitaDetalleDtoList(citaService.findByMascotaId(mascotaId)));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CitaDetalleDto>> findByClienteId(@PathVariable Long clienteId) {
        return ResponseEntity.ok(DtoMapper.toCitaDetalleDtoList(citaService.findByClienteId(clienteId)));
    }

    /** Disponibilidad de un veterinario en un día (slots de 30 min libres). */
    @GetMapping("/disponibilidad")
    public ResponseEntity<List<SlotDisponibleDto>> disponibilidad(
            @RequestParam Long veterinarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(citaService.disponibilidad(veterinarioId, fecha));
    }

    /** Admin crea una cita (queda CONFIRMADA directamente) y notifica al cliente. */
    @PostMapping
    public ResponseEntity<CitaDetalleDto> create(@Valid @RequestBody Cita cita,
                                                 @RequestParam Long clienteId,
                                                 @RequestParam Long mascotaId,
                                                 @RequestParam Long veterinarioId) {
        Cita guardada = citaService.save(cita, clienteId, mascotaId, veterinarioId);
        notificacionService.notificarAsignacion(guardada.getCliente(), guardada);
        return new ResponseEntity<>(DtoMapper.toCitaDetalleDto(guardada), HttpStatus.CREATED);
    }

    /** Cliente solicita una cita (queda PENDIENTE hasta aprobación). */
    @PostMapping("/solicitar")
    public ResponseEntity<CitaDetalleDto> solicitar(@Valid @RequestBody Cita cita,
                                                    @RequestParam Long clienteId,
                                                    @RequestParam Long mascotaId,
                                                    @RequestParam Long veterinarioId) {
        Cita solicitada = citaService.solicitar(cita, clienteId, mascotaId, veterinarioId);
        return new ResponseEntity<>(DtoMapper.toCitaDetalleDto(solicitada), HttpStatus.CREATED);
    }

    /** Admin aprueba la solicitud → CONFIRMADA + notifica al cliente. */
    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<CitaDetalleDto> aprobar(@PathVariable Long id) {
        Cita aprobada = citaService.aprobar(id);
        notificacionService.notificarAprobacion(aprobada.getCliente(), aprobada);
        return ResponseEntity.ok(DtoMapper.toCitaDetalleDto(aprobada));
    }

    /** Admin rechaza la solicitud → CANCELADA + notifica al cliente. */
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<CitaDetalleDto> rechazar(@PathVariable Long id,
                                                   @RequestParam(required = false) String motivo) {
        Cita rechazada = citaService.rechazar(id, motivo);
        notificacionService.notificarRechazo(rechazada.getCliente(), rechazada, motivo);
        return ResponseEntity.ok(DtoMapper.toCitaDetalleDto(rechazada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CitaDetalleDto> update(@PathVariable Long id, @Valid @RequestBody Cita cita) {
        return ResponseEntity.ok(DtoMapper.toCitaDetalleDto(citaService.update(id, cita)));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        citaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        citaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(citaService.contarPorRango(inicio, fin));
    }

    @GetMapping("/proximas")
    public ResponseEntity<List<CitaResumenDto>> proximas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin,
            @RequestParam(defaultValue = "5") int limite) {
        return ResponseEntity.ok(citaService.proximasEnRango(inicio, fin, limite));
    }
}
