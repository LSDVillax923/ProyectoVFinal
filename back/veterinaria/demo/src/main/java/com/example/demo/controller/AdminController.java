package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.AdminDto;
import com.example.demo.dto.DtoMapper;
import com.example.demo.entities.Admin;
import com.example.demo.service.AdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping
    public ResponseEntity<List<AdminDto>> findAll() {
        return ResponseEntity.ok(DtoMapper.toAdminDtoList(adminService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(DtoMapper.toAdminDto(adminService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<AdminDto> create(@Valid @RequestBody Admin admin) {
        Admin guardado = adminService.save(admin);
        return new ResponseEntity<>(DtoMapper.toAdminDto(guardado), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminDto> update(@PathVariable Long id, @Valid @RequestBody Admin admin) {
        return ResponseEntity.ok(DtoMapper.toAdminDto(adminService.update(id, admin)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AdminDto> login(@RequestParam String correo, @RequestParam String contrasenia) {
        Admin admin = adminService.login(correo, contrasenia);
        if (admin == null) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
        return ResponseEntity.ok(DtoMapper.toAdminDto(admin));
    }
}
