package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.entities.Cliente;
import com.example.demo.entities.Droga;
import com.example.demo.entities.Mascota;
import com.example.demo.entities.Tratamiento;
import com.example.demo.entities.Veterinario;

@DataJpaTest
class ConsultasQueryTest {

    @Autowired private DrogaRepository          drogaRepository;
    @Autowired private ClienteRepository        clienteRepository;
    @Autowired private TratamientoRepository    tratamientoRepository;
    @Autowired private MascotaRepository        mascotaRepository;
    @Autowired private VeterinarioRepository    veterinarioRepository;

    // Datos compartidos entre pruebas
    private Droga      drogaConStock;
    private Droga      drogaSinStock;
    private Droga      drogaConVentas;

    @BeforeEach
    void setUp() {
        // Drogas para pruebas de stock y ventas
        drogaConStock   = drogaRepository.save(new Droga(null, "Amoxicilina", 5.0f,  10.0f, 50, 0));
        drogaSinStock   = drogaRepository.save(new Droga(null, "Insulina",    8.0f,  20.0f,  3, 0));

        drogaConVentas  = drogaRepository.save(new Droga(null, "VitaminaC",  8.0f,  20.0f, 100, 3));
        drogaRepository.save(new Droga(null, "Paracetamol", 5.0f, 10.0f, 100, 5));

        // Clientes para prueba de búsqueda
        clienteRepository.save(new Cliente("Ana",  "García", "ana.garcia@gmail.com",  "pass", "3001111111"));
        clienteRepository.save(new Cliente("Luis", "Pérez",  "luis.perez@hotmail.com","pass", "3002222222"));
        clienteRepository.save(new Cliente("Marta","Leal",   "marta@clinica.com",     "pass", "3003333333"));

        // Datos para prueba de tratamientos programados
        Cliente cliente = clienteRepository.findAll().get(0);
        Veterinario vet = veterinarioRepository.save(
            new Veterinario("Dr. López","CC999","3009999999",
                            "lopez@vet.com","General","secret",null,"ACTIVO"));
        Mascota mascota = mascotaRepository.save(new Mascota(
            "Rex","Perro","Pastor","M",
            LocalDate.of(2020,1,1),4,30.0,null,
            Mascota.EstadoMascota.ACTIVA,null,null,cliente));

        // Tratamiento en el pasado (NO debe aparecer en programados)
        tratamientoRepository.save(new Tratamiento(
            "Gripa pasada","obs", LocalDate.now().minusDays(5),
            Tratamiento.EstadoTratamiento.COMPLETADO, mascota, vet));
        // Tratamiento hoy (SÍ debe aparecer)
        tratamientoRepository.save(new Tratamiento(
            "Vacuna anual","dosis std", LocalDate.now(),
            Tratamiento.EstadoTratamiento.PENDIENTE, mascota, vet));
        // Tratamiento futuro (SÍ debe aparecer)
        tratamientoRepository.save(new Tratamiento(
            "Control post-cirugía","obs", LocalDate.now().plusDays(7),
            Tratamiento.EstadoTratamiento.PENDIENTE, mascota, vet));
    }



    // ── CONSULTA 1

    @Test
    void testSumarVentasTotales() {
        // Droga "VitaminaC":   20 * 3  = 60
        // Droga "Paracetamol": 10 * 5  = 50
        // Otras drogas tienen vendidas=0, aportan 0
        // Total esperado: 110

        //Act
        double totalVentas = drogaRepository.sumarVentasTotales();

        //Assert
        assertEquals(110.0, totalVentas);
    }

    // ── CONSULTA 2

    @Test
    void testSumarGananciasTotales() {

        //Act
        double totalGanancias = drogaRepository.sumarGananciasTotales();

        //Assert
        assertEquals(61.0, totalGanancias);
    }

    // ── CONSULTA 3

    @Test
    void testBuscarClientesPorFiltros_porCorreo() {
        //Arrange
        String filtro = "gmail"; // solo Ana tiene correo gmail

        //Act
        List<Cliente> resultado = clienteRepository.buscarPorFiltros(filtro);

        //Assert
        assertEquals(1, resultado.size());
        assertEquals("Ana", resultado.get(0).getNombre());
    }

    // ── CONSULTA 3 (OTRO CASO)
    @Test
    void testBuscarClientesPorFiltros_porNombre() {
        //Arrange
        String filtro = "luis"; // búsqueda insensible a mayúsculas

        //Act
        List<Cliente> resultado = clienteRepository.buscarPorFiltros(filtro);

        //Assert
        assertEquals(1, resultado.size());
        assertEquals("Luis", resultado.get(0).getNombre());
    }

    // ── CONSULTA 4

    @Test
    void testFindTratamientosProgramados() {
        //Act
        List<Tratamiento> programados = tratamientoRepository.findTratamientosProgramados();

        //Assert
        assertEquals(2, programados.size());
        assertEquals("Vacuna anual",       programados.get(0).getDiagnostico());
        assertEquals("Control post-cirugía", programados.get(1).getDiagnostico());
    }

    // ── CASOS 5

    @Test
    void testBuscarClientesPorFiltros_sinCoincidencias_retornaListaVacia() {
        //Arrange
        String filtroInexistente = "xyz_que_no_existe_en_ningun_campo";

        //Act
        List<Cliente> resultado = clienteRepository.buscarPorFiltros(filtroInexistente);

        //Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // ── CASOS 6
    @Test
    void testFindTratamientosProgramados_sinTratamientosFuturos_retornaListaVacia() {
        //Arrange
        List<Tratamiento> todos = tratamientoRepository.findAll();
        todos.stream()
             .filter(t -> !t.getFecha().isBefore(LocalDate.now()))
             .forEach(t -> tratamientoRepository.delete(t));

        //Act
        List<Tratamiento> programados = tratamientoRepository.findTratamientosProgramados();

        //Assert
        assertNotNull(programados);
        assertTrue(programados.isEmpty());
    }

    // ── CASOS 7
    @Test
    void testDescontarStock_idInexistente_noAfectaNingunaFila() {
        //Arrange
        Long idQueNoExiste = 999L;

        //Act
        int filasAfectadas = drogaRepository.descontarStockSiDisponible(idQueNoExiste, 5);

        //Assert
        assertEquals(0, filasAfectadas);
    }
}