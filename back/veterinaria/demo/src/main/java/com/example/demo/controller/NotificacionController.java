package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.DtoMapper;
import com.example.demo.dto.NotificacionDto;
import com.example.demo.service.NotificacionService;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    /** Notificaciones de un cliente (campana). */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<NotificacionDto>> deCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(DtoMapper.toNotificacionDtoList(notificacionService.findByCliente(clienteId)));
    }

    /** Conteo de no leídas (badge de la campana). */
    @GetMapping("/cliente/{clienteId}/no-leidas")
    public ResponseEntity<Long> contarNoLeidas(@PathVariable Long clienteId) {
        return ResponseEntity.ok(notificacionService.contarNoLeidas(clienteId));
    }

    /** Marcar una notificación como leída. */
    @PatchMapping("/{id}/leida")
    public ResponseEntity<NotificacionDto> marcarLeida(@PathVariable Long id) {
        return ResponseEntity.ok(DtoMapper.toNotificacionDto(notificacionService.marcarLeida(id)));
    }
}
