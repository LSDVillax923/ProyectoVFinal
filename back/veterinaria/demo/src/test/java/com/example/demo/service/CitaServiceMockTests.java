package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.entities.Cita;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.Mascota;
import com.example.demo.entities.Veterinario;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.MascotaRepository;
import com.example.demo.repository.VeterinarioRepository;

@ExtendWith(MockitoExtension.class)
class CitaServiceMockTests {

    @Mock private CitaRepository citaRepository;
    @Mock private ClienteRepository clienteRepository;
    @Mock private MascotaRepository mascotaRepository;
    @Mock private VeterinarioRepository veterinarioRepository;

    @InjectMocks private CitaServiceImpl citaService;

    @Test
    void save_debeFallarCuandoHaySolapamiento() {
        Cliente c = new Cliente("A", "B", "a@b.com", "123", "3001234567"); c.setId(1L);
        Mascota m = new Mascota("L", "Can", "Lab", "H", LocalDate.now(), 2, 10, null, Mascota.EstadoMascota.ACTIVA, null, null, c); m.setId(2L);
        Veterinario v = new Veterinario("V", "1", "3001234568", "v@b.com", "Gen", "x", null, "ACTIVO"); v.setId(3L);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(c));
        when(mascotaRepository.findById(2L)).thenReturn(Optional.of(m));
        when(veterinarioRepository.findById(3L)).thenReturn(Optional.of(v));
        when(citaRepository.findCitasSolapadas(any(), any(), any())).thenReturn(List.of(new Cita()));

        Cita cita = new Cita(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), "Ctrl", null, null, null, null);
        assertThatThrownBy(() -> citaService.save(cita, 1L, 2L, 3L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ya tiene una cita");
    }
}