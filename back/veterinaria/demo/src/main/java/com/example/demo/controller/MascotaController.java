package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.DtoMapper;
import com.example.demo.dto.MascotaDto;
import com.example.demo.entities.Mascota;
import com.example.demo.service.MascotaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/mascotas")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @GetMapping
    public ResponseEntity<List<MascotaDto>> findAll(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String estado) {
        List<Mascota> resultado = ((query != null && !query.isBlank()) || (estado != null && !estado.isBlank()))
                ? mascotaService.buscarPorFiltros(query, estado)
                : mascotaService.findAll();
        return ResponseEntity.ok(DtoMapper.toMascotaDtoList(resultado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MascotaDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(DtoMapper.toMascotaDto(mascotaService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<MascotaDto> create(@Valid @RequestBody Mascota mascota,
                                             @RequestParam Long clienteId) {
        Mascota guardada = mascotaService.save(mascota, clienteId);
        return new ResponseEntity<>(DtoMapper.toMascotaDto(guardada), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MascotaDto> update(@PathVariable Long id, @Valid @RequestBody Mascota mascota) {
        return ResponseEntity.ok(DtoMapper.toMascotaDto(mascotaService.update(id, mascota)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MascotaDto> patch(@PathVariable Long id, @RequestBody Mascota mascota) {
        return ResponseEntity.ok(DtoMapper.toMascotaDto(mascotaService.patch(id, mascota)));
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
    public ResponseEntity<List<MascotaDto>> findByClienteId(@PathVariable Long clienteId) {
        return ResponseEntity.ok(DtoMapper.toMascotaDtoList(mascotaService.findByClienteId(clienteId)));
    }

    @PostMapping(value = "/{id}/foto", consumes = "multipart/form-data")
    public ResponseEntity<MascotaDto> subirFoto(@PathVariable Long id,
                                                @RequestParam("archivo") MultipartFile archivo) {
        return ResponseEntity.ok(DtoMapper.toMascotaDto(mascotaService.subirFoto(id, archivo)));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count(@RequestParam(required = false) Mascota.EstadoMascota estado) {
        long total = (estado == null)
                ? mascotaService.contar()
                : mascotaService.contarPorEstado(estado);
        return ResponseEntity.ok(total);
    }
}
