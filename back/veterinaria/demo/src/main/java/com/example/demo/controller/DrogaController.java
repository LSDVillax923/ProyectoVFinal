package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.MedicamentoCantidadDto;
import com.example.demo.entities.Droga;
import com.example.demo.service.DrogaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/drogas")
public class DrogaController {

    @Autowired
    private DrogaService drogaService;

    @GetMapping
    public ResponseEntity<List<Droga>> findAll(@RequestParam(required = false) Boolean disponibles) {
        if (Boolean.TRUE.equals(disponibles)) {
            return ResponseEntity.ok(drogaService.findDisponibles());
        }
        return ResponseEntity.ok(drogaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Droga> findById(@PathVariable Long id) {
        return ResponseEntity.ok(drogaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Droga> create(@Valid @RequestBody Droga droga) {
        return new ResponseEntity<>(drogaService.save(droga), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Droga> update(@PathVariable Long id, @Valid @RequestBody Droga droga) {
        return ResponseEntity.ok(drogaService.update(id, droga));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        drogaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/descontar")
    public ResponseEntity<Void> descontarUnidades(@PathVariable Long id, @RequestParam int cantidad) {
        drogaService.descontarUnidades(id, cantidad);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ventas-totales")
    public ResponseEntity<Double> ventasTotales() {
        return ResponseEntity.ok(drogaService.ventasTotales());
    }

    @GetMapping("/ganancias-totales")
    public ResponseEntity<Double> gananciasTotales() {
        return ResponseEntity.ok(drogaService.gananciasTotales());
    }

    @GetMapping("/top-mas-vendidos")
    public ResponseEntity<List<MedicamentoCantidadDto>> topMasVendidos(
            @RequestParam(defaultValue = "3") int limite) {
        return ResponseEntity.ok(drogaService.topMasVendidos(limite));
    }
}