package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entities.Tratamiento;
import com.example.demo.service.TratamientoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tratamientos")
public class TratamientoController {

    @Autowired
    private TratamientoService tratamientoService;

    @GetMapping
    public ResponseEntity<List<Tratamiento>> findAll(@RequestParam(required = false) Boolean programados) {
        if (Boolean.TRUE.equals(programados)) {
            return ResponseEntity.ok(tratamientoService.findProgramados());
        }
        return ResponseEntity.ok(tratamientoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tratamiento> findById(@PathVariable Long id) {
        return ResponseEntity.ok(tratamientoService.findById(id));
    }

    @GetMapping("/mascota/{mascotaId}")
    public ResponseEntity<List<Tratamiento>> findByMascotaId(@PathVariable Long mascotaId) {
        return ResponseEntity.ok(tratamientoService.findByMascotaId(mascotaId));
    }

    @GetMapping("/veterinario/{veterinarioId}")
    public ResponseEntity<List<Tratamiento>> findByVeterinarioId(@PathVariable Long veterinarioId) {
        return ResponseEntity.ok(tratamientoService.findByVeterinarioId(veterinarioId));
    }

    @PostMapping
    public ResponseEntity<Tratamiento> create(@Valid @RequestBody Tratamiento tratamiento,
                                              @RequestParam Long mascotaId,
                                              @RequestParam Long veterinarioId) {
        return new ResponseEntity<>(tratamientoService.save(tratamiento, mascotaId, veterinarioId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tratamiento> update(@PathVariable Long id, @Valid @RequestBody Tratamiento tratamiento) {
        return ResponseEntity.ok(tratamientoService.update(id, tratamiento));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tratamientoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(tratamientoService.contarPorRango(inicio, fin));
    }
}