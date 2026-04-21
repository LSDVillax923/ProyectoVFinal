package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entities.Mascota;
import com.example.demo.service.MascotaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/mascotas")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @GetMapping
    public ResponseEntity<List<Mascota>> findAll(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String estado) {
        if ((query != null && !query.isBlank()) || (estado != null && !estado.isBlank())) {
            return ResponseEntity.ok(mascotaService.buscarPorFiltros(query, estado));
        }
        return ResponseEntity.ok(mascotaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mascota> findById(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Mascota> create(@Valid @RequestBody Mascota mascota,
                                          @RequestParam Long clienteId) {
        return new ResponseEntity<>(mascotaService.save(mascota, clienteId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mascota> update(@PathVariable Long id, @Valid @RequestBody Mascota mascota) {
        return ResponseEntity.ok(mascotaService.update(id, mascota));
    }

     @PatchMapping("/{id}")
    public ResponseEntity<Mascota> patch(@PathVariable Long id, @RequestBody Mascota mascota) {
        return ResponseEntity.ok(mascotaService.patch(id, mascota));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        mascotaService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mascotaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Mascota>> findByClienteId(@PathVariable Long clienteId) {
        return ResponseEntity.ok(mascotaService.findByClienteId(clienteId));
    }
}