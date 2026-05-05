package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.Cita;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.Mascota;
import com.example.demo.entities.Tratamiento;
import com.example.demo.entities.Veterinario;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.MascotaRepository;
import com.example.demo.repository.VeterinarioRepository;

@SpringBootTest
@Transactional
class ServiceIntegrationTests {

    @Autowired private CitaService citaService;
    @Autowired private TratamientoService tratamientoService;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private MascotaRepository mascotaRepository;
    @Autowired private VeterinarioRepository veterinarioRepository;

    @Test
    void citaService_save_integracionSinMocks() {
        Cliente c = clienteRepository.save(new Cliente("Luis", "Diaz", "luis@test.com", "123456", "3001234569"));
        Veterinario v = veterinarioRepository.save(new Veterinario("Vet Uno", "1", "3001234570", "vet1@test.com", "General", "abc", null, "ACTIVO"));
        Mascota m = mascotaRepository.save(new Mascota("Nina", "Felino", "Siames", "H", LocalDate.of(2022,1,1), 4, 4.5, null, Mascota.EstadoMascota.ACTIVA, null, null, c));

        Cita cita = new Cita(LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(1), "Control", null, null, null, null);
        Cita guardada = citaService.save(cita, c.getId(), m.getId(), v.getId());

        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getEstado()).isEqualTo(Cita.EstadoCita.PENDIENTE);
    }

    @Test
    void tratamientoService_save_integracionSinMocks() {
        Cliente c = clienteRepository.save(new Cliente("Eva", "Mora", "eva@test.com", "123456", "3001234571"));
        Veterinario v = veterinarioRepository.save(new Veterinario("Vet Dos", "2", "3001234572", "vet2@test.com", "Cirugia", "abc", null, "ACTIVO"));
        Mascota m = mascotaRepository.save(new Mascota("Rocky", "Canino", "Criollo", "M", LocalDate.of(2021,1,1), 5, 14.0, null, Mascota.EstadoMascota.ACTIVA, null, null, c));

        Tratamiento tratamiento = new Tratamiento("Dermatitis", "Aplicar crema", LocalDate.now().plusDays(1), null, null, null);
        Tratamiento guardado = tratamientoService.save(tratamiento, m.getId(), v.getId());

        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getEstado()).isEqualTo(Tratamiento.EstadoTratamiento.PENDIENTE);
    }
}