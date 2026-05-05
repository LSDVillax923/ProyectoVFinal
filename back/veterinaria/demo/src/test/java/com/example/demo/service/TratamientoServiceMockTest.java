package com.example.demo.service;

import com.example.demo.entities.Mascota;
import com.example.demo.entities.Tratamiento;
import com.example.demo.entities.Veterinario;
import com.example.demo.errors.TratamientoException;
import com.example.demo.repository.MascotaRepository;
import com.example.demo.repository.TratamientoRepository;
import com.example.demo.repository.VeterinarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TratamientoServiceMockTest {

    // Repositorios simulados (mocks)
    @Mock
    private TratamientoRepository tratamientoRepository;

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private VeterinarioRepository veterinarioRepository;

    
    @InjectMocks
    private TratamientoServiceImpl tratamientoService;

    // ── TEST 1: save exitoso 

    @Test
    void testSave_exitoso() {
        //Arrange
        Mascota mascota = new Mascota();
        mascota.setId(1L);
        mascota.setEstado(Mascota.EstadoMascota.ACTIVA);

        Veterinario vet = new Veterinario();
        vet.setId(10L);

        Tratamiento tratamientoEntrada = new Tratamiento();
        tratamientoEntrada.setDiagnostico("Otitis");
        tratamientoEntrada.setFecha(LocalDate.now());

        Tratamiento tratamientoGuardado = new Tratamiento();
        tratamientoGuardado.setId(100L);
        tratamientoGuardado.setDiagnostico("Otitis");
        tratamientoGuardado.setFecha(LocalDate.now());
        tratamientoGuardado.setEstado(Tratamiento.EstadoTratamiento.PENDIENTE);

        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
        when(veterinarioRepository.findById(10L)).thenReturn(Optional.of(vet));
        when(tratamientoRepository.save(any(Tratamiento.class))).thenReturn(tratamientoGuardado);

        //Act
        Tratamiento resultado = tratamientoService.save(tratamientoEntrada, 1L, 10L);

        //Assert
        assertNotNull(resultado.getId());
        assertEquals("Otitis", resultado.getDiagnostico());
        assertEquals(Tratamiento.EstadoTratamiento.PENDIENTE, resultado.getEstado());
        verify(tratamientoRepository, times(1)).save(any(Tratamiento.class));
    }

    // ── TEST 2: save con mascota no encontrada ────────────────────────────────

    @Test
    void testSave_mascotaNoEncontrada() {
        //Arrange
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        Tratamiento t = new Tratamiento();
        t.setDiagnostico("Revisión");
        t.setFecha(LocalDate.now());

        //Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            tratamientoService.save(t, 99L, 10L)
        );
        // Nunca debe llegar a llamar al repositorio de tratamientos
        verify(tratamientoRepository, never()).save(any());
    }

    // ── TEST 3: save con mascota INACTIVA ─────────────────────────────────────

    @Test
    void testSave_mascotaInactiva() {
        //Arrange
        Mascota mascotaInactiva = new Mascota();
        mascotaInactiva.setId(1L);
        mascotaInactiva.setEstado(Mascota.EstadoMascota.INACTIVA);

        Veterinario vet = new Veterinario();
        vet.setId(10L);

        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascotaInactiva));
        when(veterinarioRepository.findById(10L)).thenReturn(Optional.of(vet));

        Tratamiento t = new Tratamiento();
        t.setDiagnostico("Control");
        t.setFecha(LocalDate.now());

        //Act & Assert
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            tratamientoService.save(t, 1L, 10L)
        );
        assertTrue(ex.getMessage().contains("inactiva"));
    }

    // ── TEST 4: findById existente ────────────────────────────────────────────

    @Test
    void testFindById_encontrado() {
        //Arrange
        Tratamiento tratamiento = new Tratamiento();
        tratamiento.setId(100L);
        tratamiento.setDiagnostico("Dermatitis");

        when(tratamientoRepository.findById(100L)).thenReturn(Optional.of(tratamiento));

        //Act
        Tratamiento resultado = tratamientoService.findById(100L);

        //Assert
        assertEquals("Dermatitis", resultado.getDiagnostico());
        verify(tratamientoRepository).findById(100L);
    }

    // ── TEST 5: findById no existente lanza excepción ─────────────────────────

    @Test
    void testFindById_noExiste() {
        //Arrange
        when(tratamientoRepository.findById(999L)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(TratamientoException.class, () ->
            tratamientoService.findById(999L)
        );
    }

    // ── TEST 6: update ────────────────────────────────────────────────────────

    @Test
    void testUpdate() {
        //Arrange
        Tratamiento existente = new Tratamiento();
        existente.setId(100L);
        existente.setDiagnostico("Diagnóstico viejo");
        existente.setFecha(LocalDate.now());
        existente.setEstado(Tratamiento.EstadoTratamiento.PENDIENTE);

        Tratamiento cambios = new Tratamiento();
        cambios.setDiagnostico("Diagnóstico nuevo");
        cambios.setFecha(LocalDate.now().plusDays(1));
        cambios.setEstado(Tratamiento.EstadoTratamiento.COMPLETADO);

        when(tratamientoRepository.findById(100L)).thenReturn(Optional.of(existente));
        when(tratamientoRepository.save(existente)).thenReturn(existente);

        //Act
        Tratamiento resultado = tratamientoService.update(100L, cambios);

        //Assert
        assertEquals("Diagnóstico nuevo", resultado.getDiagnostico());
        assertEquals(Tratamiento.EstadoTratamiento.COMPLETADO, resultado.getEstado());
        verify(tratamientoRepository).save(existente);
    }

    // ── TEST 7: delete ────────────────────────────────────────────────────────

    @Test
    void testDelete() {
        //Arrange
        Tratamiento tratamiento = new Tratamiento();
        tratamiento.setId(100L);

        when(tratamientoRepository.findById(100L)).thenReturn(Optional.of(tratamiento));
        doNothing().when(tratamientoRepository).delete(tratamiento);

        //Act
        tratamientoService.delete(100L);

        //Assert
        verify(tratamientoRepository, times(1)).delete(tratamiento);
    }

    // ── TEST 8: findAll ───────────────────────────────────────────────────────

    @Test
    void testFindAll() {
        //Arrange
        Tratamiento t1 = new Tratamiento(); t1.setId(1L); t1.setDiagnostico("A");
        Tratamiento t2 = new Tratamiento(); t2.setId(2L); t2.setDiagnostico("B");

        when(tratamientoRepository.findAll()).thenReturn(List.of(t1, t2));

        //Act
        List<Tratamiento> lista = tratamientoService.findAll();

        //Assert
        assertEquals(2, lista.size());
        verify(tratamientoRepository).findAll();
    }

    // ── TEST 9: contarPorRango ────────────────────────────────────────────────

    @Test
    void testContarPorRango() {
        //Arrange
        LocalDate inicio = LocalDate.now();
        LocalDate fin    = LocalDate.now().plusDays(7);

        when(tratamientoRepository.countByFechaBetween(inicio, fin)).thenReturn(4L);

        //Act
        long resultado = tratamientoService.contarPorRango(inicio, fin);

        //Assert
        assertEquals(4L, resultado);
        verify(tratamientoRepository).countByFechaBetween(inicio, fin);
    }
}