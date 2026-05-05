package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.entities.Cita;
import com.example.demo.service.CitaService;

@WebMvcTest(CitaController.class)
class CitaControllerWebMvcTests {

    @Autowired private MockMvc mockMvc;
    @MockBean private CitaService citaService;

    @Test
    void consultasControlador_conMockMvc() throws Exception {
        when(citaService.findAll()).thenReturn(List.of(new Cita()));
        when(citaService.findByVeterinarioId(1L)).thenReturn(List.of(new Cita()));
        when(citaService.findByMascotaId(1L)).thenReturn(List.of(new Cita()));
        when(citaService.findByClienteId(1L)).thenReturn(List.of(new Cita()));
        when(citaService.findCitasEnRango(any(), any())).thenReturn(List.of(new Cita()));

        mockMvc.perform(get("/api/citas")).andExpect(status().isOk());
        mockMvc.perform(get("/api/citas/veterinario/1")).andExpect(status().isOk());
        mockMvc.perform(get("/api/citas/mascota/1")).andExpect(status().isOk());
        mockMvc.perform(get("/api/citas/cliente/1")).andExpect(status().isOk());
        mockMvc.perform(get("/api/citas")
                .param("inicio", LocalDateTime.now().plusDays(1).toString())
                .param("fin", LocalDateTime.now().plusDays(1).plusHours(1).toString()))
                .andExpect(status().isOk());

        verify(citaService).findAll();
        verify(citaService).findByVeterinarioId(1L);
        verify(citaService).findByMascotaId(1L);
        verify(citaService).findByClienteId(1L);
        verify(citaService).findCitasEnRango(any(), any());
    }
}