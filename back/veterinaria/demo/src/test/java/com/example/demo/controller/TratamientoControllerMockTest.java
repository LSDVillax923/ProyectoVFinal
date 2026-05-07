package com.example.demo.controller;

import com.example.demo.entities.Mascota;
import com.example.demo.entities.Tratamiento;
import com.example.demo.entities.Veterinario;
import com.example.demo.errors.TratamientoException;
import com.example.demo.service.TratamientoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(TratamientoController.class)
@ExtendWith(MockitoExtension.class)

class TratamientoControllerMockTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TratamientoService tratamientoService;

    private ObjectMapper objectMapper;
    private Tratamiento tratamientoEjemplo;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Mascota mascota = new Mascota();
        mascota.setId(1L);
        mascota.setNombre("Rex");

        Veterinario vet = new Veterinario();
        vet.setId(10L);
        vet.setNombre("Dr. Gómez");

        tratamientoEjemplo = new Tratamiento();
        tratamientoEjemplo.setId(100L);
        tratamientoEjemplo.setDiagnostico("Otitis externa");
        tratamientoEjemplo.setObservaciones("Limpiar oídos cada día");
        tratamientoEjemplo.setFecha(LocalDate.of(2025, 8, 10));
        tratamientoEjemplo.setEstado(Tratamiento.EstadoTratamiento.PENDIENTE);
        tratamientoEjemplo.setMascota(mascota);
        tratamientoEjemplo.setVeterinario(vet);
    }

    // ── GET /api/tratamientos 

    @Test
    void testGetAll_retorna200ConListaDeTratamientos() throws Exception {
        //Arrange
        when(tratamientoService.findAll()).thenReturn(List.of(tratamientoEjemplo));

        //Act & Assert
        mockMvc.perform(get("/api/tratamientos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].diagnostico").value("Otitis externa"));
    }

    @Test
    void testGetAll_programados_retorna200ConTratamientosProgramados() throws Exception {
        //Arrange
        when(tratamientoService.findProgramados()).thenReturn(List.of(tratamientoEjemplo));

        //Act & Assert
        mockMvc.perform(get("/api/tratamientos").param("programados", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));

        verify(tratamientoService).findProgramados();
        verify(tratamientoService, never()).findAll();
    }

    // ── GET /api/tratamientos/{id} 

    @Test
    void testGetById_retorna200CuandoExiste() throws Exception {
        //Arrange
        when(tratamientoService.findById(100L)).thenReturn(tratamientoEjemplo);

        //Act & Assert
        mockMvc.perform(get("/api/tratamientos/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnostico").value("Otitis externa"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void testGetById_retorna404CuandoNoExiste() throws Exception {
        //Arrange
        when(tratamientoService.findById(999L))
                .thenThrow(new TratamientoException("Tratamiento no encontrado con ID: 999"));

        //Act & Assert
        mockMvc.perform(get("/api/tratamientos/999"))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/tratamientos/mascota/{mascotaId}

    @Test
    void testGetByMascotaId_retorna200ConTratamientosDeLaMascota() throws Exception {
        //Arrange
        when(tratamientoService.findByMascotaId(1L)).thenReturn(List.of(tratamientoEjemplo));

        //Act & Assert
        mockMvc.perform(get("/api/tratamientos/mascota/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].mascota.id").value(1));
    }

    // ── GET /api/tratamientos/veterinario/{veterinarioId}

    @Test
    void testGetByVeterinarioId_retorna200ConTratamientosDelVeterinario() throws Exception {
        //Arrange
        when(tratamientoService.findByVeterinarioId(10L)).thenReturn(List.of(tratamientoEjemplo));

        //Act & Assert
        mockMvc.perform(get("/api/tratamientos/veterinario/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].veterinario.id").value(10));
    }

    // ── POST /api/tratamientos 

    @Test
    void testCreate_retorna201ConTratamientoCreado() throws Exception {
        //Arrange
        Tratamiento nuevo = new Tratamiento();
        nuevo.setDiagnostico("Herida en pata");
        nuevo.setFecha(LocalDate.of(2025, 9, 1));

        when(tratamientoService.save(any(Tratamiento.class), eq(1L), eq(10L)))
                .thenReturn(tratamientoEjemplo);

        //Act & Assert
        mockMvc.perform(post("/api/tratamientos")
                        .param("mascotaId",     "1")
                        .param("veterinarioId", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.diagnostico").value("Otitis externa"));
    }

    @Test
    void testCreate_retorna400CuandoFaltaLaFecha() throws Exception {
        //Arrange — fecha es @NotNull, al omitirla Spring debe retornar 400
        Tratamiento sinFecha = new Tratamiento();
        sinFecha.setDiagnostico("Sin fecha");
        // fecha = null → viola @NotNull

        //Act & Assert
        mockMvc.perform(post("/api/tratamientos")
                        .param("mascotaId",     "1")
                        .param("veterinarioId", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sinFecha)))
                .andExpect(status().isBadRequest());

        verify(tratamientoService, never()).save(any(), any(), any());
    }

    @Test
    void testCreate_retorna400CuandoMascotaEstaInactiva() throws Exception {
        //Arrange — el servicio lanza IllegalArgumentException → GlobalExceptionHandler → 400
        Tratamiento t = new Tratamiento();
        t.setDiagnostico("Control");
        t.setFecha(LocalDate.of(2025, 9, 1));

        when(tratamientoService.save(any(Tratamiento.class), eq(1L), eq(10L)))
                .thenThrow(new IllegalArgumentException("La mascota está inactiva"));

        //Act & Assert
        mockMvc.perform(post("/api/tratamientos")
                        .param("mascotaId",     "1")
                        .param("veterinarioId", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(t)))
                .andExpect(status().isBadRequest());
    }

    // ── PUT /api/tratamientos/{id}

    @Test
    void testUpdate_retorna200ConTratamientoActualizado() throws Exception {
        //Arrange
        Tratamiento cambios = new Tratamiento();
        cambios.setDiagnostico("Diagnóstico actualizado");
        cambios.setFecha(LocalDate.of(2025, 9, 15));
        cambios.setEstado(Tratamiento.EstadoTratamiento.COMPLETADO);

        Tratamiento actualizado = new Tratamiento();
        actualizado.setId(100L);
        actualizado.setDiagnostico("Diagnóstico actualizado");
        actualizado.setFecha(LocalDate.of(2025, 9, 15));
        actualizado.setEstado(Tratamiento.EstadoTratamiento.COMPLETADO);

        when(tratamientoService.update(eq(100L), any(Tratamiento.class))).thenReturn(actualizado);

        //Act & Assert
        mockMvc.perform(put("/api/tratamientos/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cambios)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diagnostico").value("Diagnóstico actualizado"))
                .andExpect(jsonPath("$.estado").value("COMPLETADO"));
    }

    @Test
    void testUpdate_retorna404CuandoNoExiste() throws Exception {
        //Arrange
        Tratamiento cambios = new Tratamiento();
        cambios.setDiagnostico("Cambio inútil");
        cambios.setFecha(LocalDate.of(2025, 9, 15));

        when(tratamientoService.update(eq(999L), any(Tratamiento.class)))
                .thenThrow(new TratamientoException("Tratamiento no encontrado con ID: 999"));

        //Act & Assert
        mockMvc.perform(put("/api/tratamientos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cambios)))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/tratamientos/{id} 

    @Test
    void testDelete_retorna204CuandoEliminaCorrectamente() throws Exception {
        //Arrange
        doNothing().when(tratamientoService).delete(100L);

        //Act & Assert
        mockMvc.perform(delete("/api/tratamientos/100"))
                .andExpect(status().isNoContent());

        verify(tratamientoService, times(1)).delete(100L);
    }

    @Test
    void testDelete_retorna404CuandoNoExiste() throws Exception {
        //Arrange
        doThrow(new TratamientoException("Tratamiento no encontrado con ID: 999"))
                .when(tratamientoService).delete(999L);

        //Act & Assert
        mockMvc.perform(delete("/api/tratamientos/999"))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/tratamientos/count

    @Test
    void testCount_retorna200ConCantidadDeTratamientos() throws Exception {
        //Arrange
        when(tratamientoService.contarPorRango(
                LocalDate.of(2025, 8, 1),
                LocalDate.of(2025, 8, 31)))
                .thenReturn(7L);

        //Act & Assert
        mockMvc.perform(get("/api/tratamientos/count")
                        .param("inicio", "2025-08-01")
                        .param("fin",    "2025-08-31"))
                .andExpect(status().isOk())
                .andExpect(content().string("7"));
    }
}