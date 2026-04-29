package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.CitaResumenDto;
import com.example.demo.entities.Cita;
import com.example.demo.service.CitaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    @GetMapping
    public ResponseEntity<List<Cita>> findAll(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        if (inicio != null && fin != null) {
            return ResponseEntity.ok(citaService.findCitasEnRango(inicio, fin));
        }
        return ResponseEntity.ok(citaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cita> findById(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.findById(id));
    }

    @GetMapping("/veterinario/{veterinarioId}")
    public ResponseEntity<List<Cita>> findByVeterinarioId(@PathVariable Long veterinarioId) {
        return ResponseEntity.ok(citaService.findByVeterinarioId(veterinarioId));
    }

    @GetMapping("/mascota/{mascotaId}")
    public ResponseEntity<List<Cita>> findByMascotaId(@PathVariable Long mascotaId) {
        return ResponseEntity.ok(citaService.findByMascotaId(mascotaId));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Cita>> findByClienteId(@PathVariable Long clienteId) {
        return ResponseEntity.ok(citaService.findByClienteId(clienteId));
    }

    @PostMapping
    public ResponseEntity<Cita> create(@Valid @RequestBody Cita cita,
                                       @RequestParam Long clienteId,
                                       @RequestParam Long mascotaId,
                                       @RequestParam Long veterinarioId) {
        return new ResponseEntity<>(citaService.save(cita, clienteId, mascotaId, veterinarioId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cita> update(@PathVariable Long id, @Valid @RequestBody Cita cita) {
        return ResponseEntity.ok(citaService.update(id, cita));
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