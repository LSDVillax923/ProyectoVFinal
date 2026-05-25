package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.ClienteDto;
import com.example.demo.dto.DtoMapper;
import com.example.demo.dto.MascotaDto;
import com.example.demo.entities.Cliente;
import com.example.demo.repository.MascotaRepository;
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

    @Autowired
    private MascotaRepository mascotaRepository;

    @GetMapping
    public ResponseEntity<List<ClienteDto>> findAll(@RequestParam(required = false) String query) {
        List<Cliente> resultado = (query != null && !query.isBlank())
                ? clienteService.buscarPorFiltros(query)
                : clienteService.findAll();

        Map<Long, Long> mascotasPorCliente = new HashMap<>();
        for (Cliente c : resultado) {
            mascotasPorCliente.put(c.getId(), mascotaRepository.countByCliente_Id(c.getId()));
        }
        return ResponseEntity.ok(DtoMapper.toClienteDtoList(resultado, mascotasPorCliente));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDto> findById(@PathVariable Long id) {
        Cliente c = clienteService.findById(id);
        long count = mascotaRepository.countByCliente_Id(id);
        return ResponseEntity.ok(DtoMapper.toClienteDto(c, count));
    }

    @PostMapping
    public ResponseEntity<ClienteDto> create(@Valid @RequestBody Cliente cliente) {
        Cliente guardado = clienteService.save(cliente);
        return new ResponseEntity<>(DtoMapper.toClienteDto(guardado, 0L), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDto> update(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        Cliente actualizado = clienteService.update(id, cliente);
        long count = mascotaRepository.countByCliente_Id(id);
        return ResponseEntity.ok(DtoMapper.toClienteDto(actualizado, count));
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
        long count = mascotaRepository.countByCliente_Id(cliente.getId());
        return ResponseEntity.ok(DtoMapper.toClienteDto(cliente, count));
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
