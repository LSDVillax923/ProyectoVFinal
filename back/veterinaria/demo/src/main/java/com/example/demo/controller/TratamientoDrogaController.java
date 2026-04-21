package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entities.TratamientoDroga;
import com.example.demo.service.TratamientoDrogaService;

@RestController
@RequestMapping("/api/tratamiento-drogas")
public class TratamientoDrogaController {

    @Autowired
    private TratamientoDrogaService tdService;

    @GetMapping("/{id}")
    public ResponseEntity<TratamientoDroga> findById(@PathVariable Long id) {
        return ResponseEntity.ok(tdService.findById(id));
    }

    @GetMapping("/tratamiento/{tratamientoId}")
    public ResponseEntity<List<TratamientoDroga>> findByTratamientoId(@PathVariable Long tratamientoId) {
        return ResponseEntity.ok(tdService.findByTratamientoId(tratamientoId));
    }

    @PostMapping
    public ResponseEntity<TratamientoDroga> agregarDroga(@RequestParam Long tratamientoId,
                                                         @RequestParam Long drogaId,
                                                         @RequestParam int cantidad) {
        return new ResponseEntity<>(tdService.agregarDroga(tratamientoId, drogaId, cantidad), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDroga(@PathVariable Long id) {
        tdService.eliminarDroga(id);
        return ResponseEntity.noContent().build();
    }
}