package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.entities.Cliente;
import com.example.demo.entities.Mascota;
import com.example.demo.entities.Tratamiento;
import com.example.demo.entities.Veterinario;
import com.example.demo.errors.TratamientoException;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.MascotaRepository;
import com.example.demo.repository.TratamientoRepository;
import com.example.demo.repository.VeterinarioRepository;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class TratamientoServiceIntegrationTest {

    @Autowired private TratamientoService    tratamientoService;
    @Autowired private MascotaRepository     mascotaRepository;
    @Autowired private VeterinarioRepository veterinarioRepository;
    @Autowired private ClienteRepository     clienteRepository;
    @Autowired private TratamientoRepository tratamientoRepository;

    private Mascota    mascota;
    private Veterinario veterinario;

    @BeforeEach
    void setUp() {
        // Se insertan los datos base que necesitan todas las pruebas
        Cliente cliente = clienteRepository.save(
            new Cliente("Juan","Torres","juan@test.com","pass123","3001234567"));

        mascota = mascotaRepository.save(new Mascota(
            "Rex","Perro","Pastor","M",
            LocalDate.of(2019,3,15), 5, 30.0, null,
            Mascota.EstadoMascota.ACTIVA, null, null, cliente));

        veterinario = veterinarioRepository.save(new Veterinario(
            "Dra. Ruiz","CC123","3109876543",
            "ruiz@vet.com","Cirugía","secret",null,"ACTIVO"));
    }

    // ── TEST 1: guardar un tratamiento 

    @Test
    void testSave() {
        //Arrange
        Tratamiento t = new Tratamiento();
        t.setDiagnostico("Otitis externa");
        t.setObservaciones("Limpiar oídos diario");
        t.setFecha(LocalDate.now());

        //Act
        Tratamiento guardado = tratamientoService.save(t, mascota.getId(), veterinario.getId());

        //Assert
        assertNotNull(guardado.getId());
        assertEquals("Otitis externa", guardado.getDiagnostico());
        assertEquals(Tratamiento.EstadoTratamiento.PENDIENTE, guardado.getEstado());
        assertEquals(mascota.getId(),    guardado.getMascota().getId());
        assertEquals(veterinario.getId(),guardado.getVeterinario().getId());
    }

    // ── TEST 2: no se puede asignar tratamiento a mascota INACTIVA ───────────

    @Test
    void testSave_mascotaInactiva_lanzaExcepcion() {
        //Arrange
        mascota.setEstado(Mascota.EstadoMascota.INACTIVA);
        mascotaRepository.save(mascota);

        Tratamiento t = new Tratamiento();
        t.setDiagnostico("Revisión");
        t.setFecha(LocalDate.now());

        //Act & Assert
        Exception excepcion = assertThrows(IllegalArgumentException.class, () ->
            tratamientoService.save(t, mascota.getId(), veterinario.getId())
        );
        assertTrue(excepcion.getMessage().contains("inactiva"));
    }

    // ── TEST 3: buscar por ID ─────────────────────────────────────────────────

    @Test
    void testFindById() {
        //Arrange
        Tratamiento t = new Tratamiento();
        t.setDiagnostico("Fiebre");
        t.setFecha(LocalDate.now());
        Tratamiento guardado = tratamientoService.save(t, mascota.getId(), veterinario.getId());

        //Act
        Tratamiento encontrado = tratamientoService.findById(guardado.getId());

        //Assert
        assertNotNull(encontrado);
        assertEquals("Fiebre", encontrado.getDiagnostico());
    }

    // ── TEST 4: buscar ID inexistente lanza excepción ─────────────────────────

    @Test
    void testFindById_noExiste_lanzaExcepcion() {
        //Arrange
        Long idInexistente = 999L;

        //Act & Assert
        assertThrows(TratamientoException.class, () ->
            tratamientoService.findById(idInexistente)
        );
    }

    // ── TEST 5: actualizar un tratamiento ─────────────────────────────────────

    @Test
    void testUpdate() {
        //Arrange
        Tratamiento t = new Tratamiento();
        t.setDiagnostico("Diagnóstico inicial");
        t.setFecha(LocalDate.now());
        Tratamiento guardado = tratamientoService.save(t, mascota.getId(), veterinario.getId());

        Tratamiento cambios = new Tratamiento();
        cambios.setDiagnostico("Diagnóstico corregido");
        cambios.setFecha(LocalDate.now().plusDays(1));
        cambios.setEstado(Tratamiento.EstadoTratamiento.COMPLETADO);

        //Act
        Tratamiento actualizado = tratamientoService.update(guardado.getId(), cambios);

        //Assert
        assertEquals("Diagnóstico corregido", actualizado.getDiagnostico());
        assertEquals(Tratamiento.EstadoTratamiento.COMPLETADO, actualizado.getEstado());
    }

    // ── TEST 6: eliminar un tratamiento ───────────────────────────────────────

    @Test
    void testDelete() {
        //Arrange
        Tratamiento t = new Tratamiento();
        t.setDiagnostico("Herida leve");
        t.setFecha(LocalDate.now());
        Tratamiento guardado = tratamientoService.save(t, mascota.getId(), veterinario.getId());
        Long id = guardado.getId();

        //Act
        tratamientoService.delete(id);

        //Assert
        assertFalse(tratamientoRepository.findById(id).isPresent());
    }

    // ── TEST 7: contar tratamientos en un rango de fechas ────────────────────

    @Test
    void testContarPorRango() {
        //Arrange
        Tratamiento t1 = new Tratamiento(); t1.setDiagnostico("A"); t1.setFecha(LocalDate.now());
        Tratamiento t2 = new Tratamiento(); t2.setDiagnostico("B"); t2.setFecha(LocalDate.now().plusDays(2));
        Tratamiento t3 = new Tratamiento(); t3.setDiagnostico("C"); t3.setFecha(LocalDate.now().plusDays(10));
        tratamientoService.save(t1, mascota.getId(), veterinario.getId());
        tratamientoService.save(t2, mascota.getId(), veterinario.getId());
        tratamientoService.save(t3, mascota.getId(), veterinario.getId());

        //Act
        long count = tratamientoService.contarPorRango(LocalDate.now(), LocalDate.now().plusDays(5));

        //Assert
        assertEquals(2, count); // solo t1 y t2 caen en el rango
    }

    // ── TEST 8: listar tratamientos por mascota ───────────────────────────────

    @Test
    void testFindByMascotaId() {
        //Arrange
        Cliente cliente2 = clienteRepository.save(
            new Cliente("Laura","Niño","laura@test.com","pass","3112233445"));
        Mascota otraMascota = mascotaRepository.save(new Mascota(
            "Luna","Gato","Persa","F",
            LocalDate.of(2021,6,1),3,4.0,null,
            Mascota.EstadoMascota.ACTIVA,null,null,cliente2));

        Tratamiento tRex  = new Tratamiento(); tRex.setDiagnostico("Control Rex");  tRex.setFecha(LocalDate.now());
        Tratamiento tLuna = new Tratamiento(); tLuna.setDiagnostico("Control Luna"); tLuna.setFecha(LocalDate.now());
        tratamientoService.save(tRex,  mascota.getId(),     veterinario.getId());
        tratamientoService.save(tLuna, otraMascota.getId(), veterinario.getId());

        //Act
        List<Tratamiento> resultados = tratamientoService.findByMascotaId(mascota.getId());

        //Assert
        assertEquals(1, resultados.size());
        assertEquals("Control Rex", resultados.get(0).getDiagnostico());
    }
}