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
        return ResponseEntity.ok(DtoMapper.toClienteDtoList(resultado, mascotasPorCliente()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDto> findById(@PathVariable Long id) {
        Cliente cliente = clienteService.findById(id);
        long count = mascotaRepository.countByCliente_Id(cliente.getId());
        return ResponseEntity.ok(DtoMapper.toClienteDto(cliente, count));
    }

    /**
     * Carga el conteo de mascotas por cliente con una sola consulta agregada.
     * Sin este map, el DtoMapper deja {@code mascotasCount = 0} en el listado.
     */
    private Map<Long, Long> mascotasPorCliente() {
        Map<Long, Long> map = new HashMap<>();
        for (Object[] row : mascotaRepository.contarMascotasPorCliente()) {
            map.put((Long) row[0], (Long) row[1]);
        }
        return map;
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
