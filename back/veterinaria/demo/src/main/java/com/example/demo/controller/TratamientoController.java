package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.DtoMapper;
import com.example.demo.dto.TratamientoDto;
import com.example.demo.entities.Tratamiento;
import com.example.demo.service.TratamientoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tratamientos")
public class TratamientoController {

    @Autowired
    private TratamientoService tratamientoService;

    @GetMapping
    public ResponseEntity<List<TratamientoDto>> findAll(@RequestParam(required = false) Boolean programados) {
        List<Tratamiento> resultado = Boolean.TRUE.equals(programados)
                ? tratamientoService.findProgramados()
                : tratamientoService.findAll();
        return ResponseEntity.ok(DtoMapper.toTratamientoDtoList(resultado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TratamientoDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(DtoMapper.toTratamientoDto(tratamientoService.findById(id)));
    }

    @GetMapping("/mascota/{mascotaId}")
    public ResponseEntity<List<TratamientoDto>> findByMascotaId(@PathVariable Long mascotaId) {
        return ResponseEntity.ok(DtoMapper.toTratamientoDtoList(tratamientoService.findByMascotaId(mascotaId)));
    }

    @GetMapping("/veterinario/{veterinarioId}")
    public ResponseEntity<List<TratamientoDto>> findByVeterinarioId(@PathVariable Long veterinarioId) {
        return ResponseEntity.ok(DtoMapper.toTratamientoDtoList(tratamientoService.findByVeterinarioId(veterinarioId)));
    }

    @PostMapping
    public ResponseEntity<TratamientoDto> create(@Valid @RequestBody Tratamiento tratamiento,
                                                 @RequestParam Long mascotaId,
                                                 @RequestParam Long veterinarioId) {
        Tratamiento guardado = tratamientoService.save(tratamiento, mascotaId, veterinarioId);
        return new ResponseEntity<>(DtoMapper.toTratamientoDto(guardado), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TratamientoDto> update(@PathVariable Long id, @Valid @RequestBody Tratamiento tratamiento) {
        return ResponseEntity.ok(DtoMapper.toTratamientoDto(tratamientoService.update(id, tratamiento)));
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
