package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.entities.Droga;
import com.example.demo.entities.Mascota;
import com.example.demo.entities.Tratamiento;
import com.example.demo.entities.TratamientoDroga;
import com.example.demo.entities.Veterinario;
import com.example.demo.errors.TratamientoException;
import com.example.demo.repository.DrogaRepository;
import com.example.demo.repository.MascotaRepository;
import com.example.demo.repository.TratamientoDrogaRepository;
import com.example.demo.repository.TratamientoRepository;
import com.example.demo.repository.VeterinarioRepository;


@ExtendWith(MockitoExtension.class)

class TratamientoServiceMockTest {

    // ── Mocks para TratamientoServiceImpl
    @Mock
    private TratamientoRepository tratamientoRepository;

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private VeterinarioRepository veterinarioRepository;

    @InjectMocks
    private TratamientoServiceImpl tratamientoService;

    // ── Mocks para TratamientoDrogaServiceImpl ────────────────────────────────
    @Mock
    private TratamientoDrogaRepository tdRepository;

    @Mock
    private DrogaRepository drogaRepository;

    @Mock
    private DrogaService drogaService;

    @InjectMocks
    private TratamientoDrogaServiceImpl tdService;

    // ── TEST 1: save exitoso ──────────────────────────────────────────────────

    @Test
    void testSave_exitoso() {
        //Arrange
        Mascota mascota = new Mascota();
        mascota.setId(1L);
        mascota.setEstado(Mascota.EstadoMascota.ACTIVA);

        Veterinario vet = new Veterinario();
        vet.setId(10L);
        vet.setEstado("ACTIVO");

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
        vet.setEstado("ACTIVO");

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
        verify(tratamientoRepository, never()).save(any());
    }

    // ── TEST 4: save con veterinario no encontrado ────────────────────────────

    @Test
    void testSave_veterinarioNoEncontrado() {
        //Arrange
        Mascota mascota = new Mascota();
        mascota.setId(1L);
        mascota.setEstado(Mascota.EstadoMascota.ACTIVA);

        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
        when(veterinarioRepository.findById(99L)).thenReturn(Optional.empty());

        Tratamiento t = new Tratamiento();
        t.setDiagnostico("Control");
        t.setFecha(LocalDate.now());

        //Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            tratamientoService.save(t, 1L, 99L)
        );
        verify(tratamientoRepository, never()).save(any());
    }

    // ── TEST 5: save con veterinario INACTIVO ─────────────────────────────────

    @Test
    void testSave_veterinarioInactivo() {
        //Arrange
        Mascota mascota = new Mascota();
        mascota.setId(1L);
        mascota.setEstado(Mascota.EstadoMascota.ACTIVA);

        Veterinario vetInactivo = new Veterinario();
        vetInactivo.setId(10L);
        vetInactivo.setEstado("INACTIVO");

        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
        when(veterinarioRepository.findById(10L)).thenReturn(Optional.of(vetInactivo));

        Tratamiento t = new Tratamiento();
        t.setDiagnostico("Control");
        t.setFecha(LocalDate.now());

        //Act & Assert
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            tratamientoService.save(t, 1L, 10L)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("inactivo"));
        verify(tratamientoRepository, never()).save(any());
    }

    // ── TEST 6: save con diagnóstico vacío ────────────────────────────────────

    @Test
    void testSave_diagnosticoVacio() {
        //Arrange
        Mascota mascota = new Mascota();
        mascota.setId(1L);
        mascota.setEstado(Mascota.EstadoMascota.ACTIVA);

        Veterinario vet = new Veterinario();
        vet.setId(10L);
        vet.setEstado("ACTIVO");

        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));
        when(veterinarioRepository.findById(10L)).thenReturn(Optional.of(vet));

        Tratamiento t = new Tratamiento();
        t.setDiagnostico("   ");
        t.setFecha(LocalDate.now());

        //Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            tratamientoService.save(t, 1L, 10L)
        );
        verify(tratamientoRepository, never()).save(any());
    }

    // ── TEST 7: agregar droga descuenta stock ─────────────────────────────────

    @Test
    void testAgregarDroga_exitoso_descontarUnidadesLlamado() {
        //Arrange
        Tratamiento t = new Tratamiento();
        t.setId(100L);
        t.setDiagnostico("Infección");

        Droga droga = new Droga(null,"Amoxicilina",5.0f,12.0f,100,0);
        droga.setId(20L);

        when(tratamientoRepository.findById(100L)).thenReturn(Optional.of(t));
        when(drogaRepository.findById(20L)).thenReturn(Optional.of(droga));
        doNothing().when(drogaService).descontarUnidades(20L, 10);
        when(tdRepository.save(any())).thenReturn(new TratamientoDroga(t, droga, 10));

        //Act
        TratamientoDroga td = tdService.agregarDroga(100L, 20L, 10);

        //Assert
        assertNotNull(td);
        // descontarUnidades debe llamarse exactamente una vez con los parámetros correctos
        verify(drogaService, times(1)).descontarUnidades(20L, 10);
        verify(tdRepository, times(1)).save(any(TratamientoDroga.class));
    }

    // ── TEST 8: stock insuficiente no guarda la línea ─────────────────────────

    @Test
    void testAgregarDroga_stockInsuficiente_tdRepositoryNuncaLlamado() {
        //Arrange
        Tratamiento t = new Tratamiento();
        t.setId(100L);
        t.setDiagnostico("Diabetes");

        Droga droga = new Droga(null,"Insulina",8.0f,20.0f,3,0);
        droga.setId(20L);

        when(tratamientoRepository.findById(100L)).thenReturn(Optional.of(t));
        when(drogaRepository.findById(20L)).thenReturn(Optional.of(droga));
        doThrow(new IllegalArgumentException("Stock insuficiente para Insulina"))
            .when(drogaService).descontarUnidades(20L, 10);

        //Act & Assert
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
            tdService.agregarDroga(100L, 20L, 10)
        );
        assertTrue(ex.getMessage().toLowerCase().contains("stock insuficiente"));
        // Si no hay stock, la línea NO debe guardarse
        verify(tdRepository, never()).save(any());
    }

    // ── TEST 9: cantidad cero no llama a descontarUnidades ───────────────────

    @Test
    void testAgregarDroga_cantidadCero_descontarUnidadesNuncaLlamado() {
        //Arrange
        Tratamiento t = new Tratamiento();
        t.setId(100L);

        Droga droga = new Droga(null,"Penicilina",4.0f,9.0f,50,0);
        droga.setId(20L);

        when(tratamientoRepository.findById(100L)).thenReturn(Optional.of(t));
        when(drogaRepository.findById(20L)).thenReturn(Optional.of(droga));

        //Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            tdService.agregarDroga(100L, 20L, 0)
        );
        verify(drogaService, never()).descontarUnidades(anyLong(), anyInt());
        verify(tdRepository,  never()).save(any());
    }

    // ── TEST 10: findById existente ───────────────────────────────────────────

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

    // ── TEST 11: findById no existente lanza excepción ────────────────────────

    @Test
    void testFindById_noExiste() {
        //Arrange
        when(tratamientoRepository.findById(999L)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(TratamientoException.class, () ->
            tratamientoService.findById(999L)
        );
    }

    // ── TEST 12: update ───────────────────────────────────────────────────────

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

    // ── TEST 13: update ID inexistente no llama a save ────────────────────────

    @Test
    void testUpdate_idInexistente() {
        //Arrange
        when(tratamientoRepository.findById(999L)).thenReturn(Optional.empty());

        Tratamiento cambios = new Tratamiento();
        cambios.setDiagnostico("Cambio sin destino");
        cambios.setFecha(LocalDate.now());

        //Act & Assert
        assertThrows(TratamientoException.class, () ->
            tratamientoService.update(999L, cambios)
        );
        verify(tratamientoRepository, never()).save(any());
    }

    // ── TEST 14: delete ───────────────────────────────────────────────────────

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

    // ── TEST 15: delete ID inexistente no llama a delete físico ──────────────

    @Test
    void testDelete_idInexistente() {
        //Arrange
        when(tratamientoRepository.findById(999L)).thenReturn(Optional.empty());

        //Act & Assert
        assertThrows(TratamientoException.class, () ->
            tratamientoService.delete(999L)
        );
        verify(tratamientoRepository, never()).delete(any(Tratamiento.class));
    }

    // ── TEST 16: findAll ──────────────────────────────────────────────────────

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

    // ── TEST 17: contarPorRango 

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