package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.DtoMapper;
import com.example.demo.dto.VeterinarioDto;
import com.example.demo.entities.Veterinario;
import com.example.demo.service.VeterinarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/veterinarios")
public class VeterinarioController {

    @Autowired
    private VeterinarioService veterinarioService;

    @GetMapping
    public ResponseEntity<List<VeterinarioDto>> findAll(@RequestParam(required = false) String estado) {
        List<Veterinario> resultado = "activo".equalsIgnoreCase(estado)
                ? veterinarioService.findActivos()
                : veterinarioService.findAll();
        return ResponseEntity.ok(DtoMapper.toVeterinarioDtoList(resultado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(DtoMapper.toVeterinarioDto(veterinarioService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<VeterinarioDto> create(@Valid @RequestBody Veterinario veterinario) {
        Veterinario guardado = veterinarioService.save(veterinario);
        return new ResponseEntity<>(DtoMapper.toVeterinarioDto(guardado), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeterinarioDto> update(@PathVariable Long id, @Valid @RequestBody Veterinario veterinario) {
        return ResponseEntity.ok(DtoMapper.toVeterinarioDto(veterinarioService.update(id, veterinario)));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<VeterinarioDto> cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        veterinarioService.cambiarEstado(id, estado);
        return ResponseEntity.ok(DtoMapper.toVeterinarioDto(veterinarioService.findById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        veterinarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<VeterinarioDto> login(@RequestParam String correo, @RequestParam String contrasenia) {
        Veterinario vet = veterinarioService.login(correo, contrasenia);
        if (vet == null) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
        return ResponseEntity.ok(DtoMapper.toVeterinarioDto(vet));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count(@RequestParam String estado) {
        return ResponseEntity.ok(veterinarioService.contarPorEstado(estado));
    }
}
