package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.entities.Cliente;
import com.example.demo.entities.Mascota;
import com.example.demo.service.ClienteService;
import com.example.demo.service.MascotaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private MascotaService mascotaService;

    @GetMapping
    public ResponseEntity<List<Cliente>> findAll(@RequestParam(required = false) String query) {
        if (query != null && !query.isBlank()) {
            return ResponseEntity.ok(clienteService.buscarPorFiltros(query));
        }
        return ResponseEntity.ok(clienteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> findById(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Cliente> create(@Valid @RequestBody Cliente cliente) {
        return new ResponseEntity<>(clienteService.save(cliente), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> update(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        return ResponseEntity.ok(clienteService.update(id, cliente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Cliente> login(@RequestParam String correo, @RequestParam String contrasenia) {
        Cliente cliente = clienteService.login(correo, contrasenia);
        if (cliente == null) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/{id}/mascotas")
    public ResponseEntity<List<Mascota>> getMascotasByCliente(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.findByClienteId(id));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(clienteService.contar());
    }
}