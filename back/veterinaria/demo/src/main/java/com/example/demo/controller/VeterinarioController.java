package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entities.Veterinario;
import com.example.demo.service.VeterinarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/veterinarios")
public class VeterinarioController {

    @Autowired
    private VeterinarioService veterinarioService;

    @GetMapping
    public ResponseEntity<List<Veterinario>> findAll(@RequestParam(required = false) String estado) {
        if ("activo".equalsIgnoreCase(estado)) {
            return ResponseEntity.ok(veterinarioService.findActivos());
        }
        return ResponseEntity.ok(veterinarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Veterinario> findById(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Veterinario> create(@Valid @RequestBody Veterinario veterinario) {
        return new ResponseEntity<>(veterinarioService.save(veterinario), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Veterinario> update(@PathVariable Long id, @Valid @RequestBody Veterinario veterinario) {
        return ResponseEntity.ok(veterinarioService.update(id, veterinario));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Veterinario> cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        veterinarioService.cambiarEstado(id, estado);
        return ResponseEntity.ok(veterinarioService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        veterinarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Veterinario> login(@RequestParam String correo, @RequestParam String contrasenia) {
        Veterinario vet = veterinarioService.login(correo, contrasenia);
        if (vet == null) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
        return ResponseEntity.ok(vet);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count(@RequestParam String estado) {
        return ResponseEntity.ok(veterinarioService.contarPorEstado(estado));
    }
}