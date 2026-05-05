package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.entities.Cita;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.Droga;
import com.example.demo.entities.Mascota;
import com.example.demo.entities.Tratamiento;
import com.example.demo.entities.Veterinario;

@DataJpaTest
class RepositoryCrudAndQueryTests {
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private MascotaRepository mascotaRepository;
    @Autowired private VeterinarioRepository veterinarioRepository;
    @Autowired private CitaRepository citaRepository;
    @Autowired private TratamientoRepository tratamientoRepository;
    @Autowired private DrogaRepository drogaRepository;

    private Cliente cliente;
    private Mascota mascota;
    private Veterinario vet;

    @BeforeEach
    void setup() {
        cliente = clienteRepository.save(new Cliente("Ana", "Perez", "ana@test.com", "123456", "3001234567"));
        vet = veterinarioRepository.save(new Veterinario("Dr. House", "123", "3001234568", "vet@test.com", "General", "abc", null, "ACTIVO"));
        mascota = mascotaRepository.save(new Mascota("Luna", "Canino", "Labrador", "H", LocalDate.of(2020, 1, 1), 6, 20.5, null,
                Mascota.EstadoMascota.ACTIVA, null, null, cliente));
    }

    @Test
    void crudMascota_debeCrearLeerActualizarEliminar() {
        Mascota creada = mascotaRepository.save(new Mascota("Max", "Canino", "Criollo", "M", LocalDate.of(2021, 2, 2), 5, 11.2, null,
                Mascota.EstadoMascota.ACTIVA, null, null, cliente));
        Mascota encontrada = mascotaRepository.findById(creada.getId()).orElseThrow();
        encontrada.setRaza("Beagle");
        mascotaRepository.save(encontrada);
        mascotaRepository.delete(encontrada);

        assertThat(mascotaRepository.findById(creada.getId())).isEmpty();
    }

    @Test
    void queryMascota_buscarPorFiltros_debeFiltrarPorQueryYEstado() {
        assertThat(mascotaRepository.buscarPorFiltros("lun", "ACTIVA")).hasSize(1);
    }

    @Test
    void queryCliente_buscarPorFiltros_debeEncontrarPorNombre() {
        assertThat(clienteRepository.buscarPorFiltros("ana")).hasSize(1);
    }

    @Test
    void queryCita_findCitasSolapadas_debeRetornarCoincidencias() {
        Cita cita = new Cita(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1),
                "Control", Cita.EstadoCita.PENDIENTE, cliente, mascota, vet);
        citaRepository.save(cita);
        assertThat(citaRepository.findCitasSolapadas(vet.getId(), cita.getFechaInicio().minusMinutes(10), cita.getFechaFin().minusMinutes(10))).hasSize(1);
    }

    @Test
    void queryTratamiento_programados_debeRetornarSoloFuturos() {
        tratamientoRepository.save(new Tratamiento("pasado", "obs", LocalDate.now().minusDays(1), Tratamiento.EstadoTratamiento.PENDIENTE, mascota, vet));
        tratamientoRepository.save(new Tratamiento("futuro", "obs", LocalDate.now().plusDays(1), Tratamiento.EstadoTratamiento.PENDIENTE, mascota, vet));

        List<Tratamiento> programados = tratamientoRepository.findTratamientosProgramados();
        assertThat(programados).extracting(Tratamiento::getDiagnostico).contains("futuro").doesNotContain("pasado");
    }

    @Test
    void queryDroga_descontarStockSiDisponible_debeActualizar() {
        Droga droga = drogaRepository.save(new Droga(null, "Amoxicilina", 10f, 15f, 20, 2));
        int filas = drogaRepository.descontarStockSiDisponible(droga.getId(), 5);
        Droga actualizada = drogaRepository.findById(droga.getId()).orElseThrow();

        assertThat(filas).isEqualTo(1);
        assertThat(actualizada.getUnidadesDisponibles()).isEqualTo(15);
    }
}