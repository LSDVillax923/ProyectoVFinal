package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ClienteDto;
import com.example.demo.dto.DtoMapper;
import com.example.demo.dto.MascotaDto;
import com.example.demo.entities.Cliente;
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
    public ResponseEntity<List<ClienteDto>> findAll(@RequestParam(required = false) String query) {
        List<Cliente> resultado = (query != null && !query.isBlank())
                ? clienteService.buscarPorFiltros(query)
                : clienteService.findAll();
        return ResponseEntity.ok(DtoMapper.toClienteDtoList(resultado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(DtoMapper.toClienteDto(clienteService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ClienteDto> create(@Valid @RequestBody Cliente cliente) {
        Cliente guardado = clienteService.save(cliente);
        return new ResponseEntity<>(DtoMapper.toClienteDto(guardado), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDto> update(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        return ResponseEntity.ok(DtoMapper.toClienteDto(clienteService.update(id, cliente)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<ClienteDto> login(
            @RequestParam(required = false) String identificador,
            @RequestParam(required = false) String correo,
            @RequestParam String contrasenia) {
        // Compat: si llega 'correo' (clientes antiguos), se usa como identificador.
        String id = (identificador != null && !identificador.isBlank()) ? identificador : correo;
        Cliente cliente = clienteService.login(id, contrasenia);
        if (cliente == null) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
        return ResponseEntity.ok(DtoMapper.toClienteDto(cliente));
    }

    @GetMapping("/{id}/mascotas")
    public ResponseEntity<List<MascotaDto>> getMascotasByCliente(@PathVariable Long id) {
        return ResponseEntity.ok(DtoMapper.toMascotaDtoList(mascotaService.findByClienteId(id)));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(clienteService.contar());
    }
}
