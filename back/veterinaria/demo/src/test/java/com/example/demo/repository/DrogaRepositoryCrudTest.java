package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.demo.entities.Droga;


@DataJpaTest
@RunWith(org.springframework.test.context.junit4.SpringRunner.class)
class DrogaRepositoryCrudTest {

    @Autowired
    private DrogaRepository drogaRepository;

    @BeforeEach
    void setUp() {
        Droga amoxicilina = new Droga(null, "Amoxicilina", 5.0f, 12.0f, 100, 0);
        Droga ibuprofeno  = new Droga(null, "Ibuprofeno",  3.0f,  8.0f,  50, 10);
        drogaRepository.save(amoxicilina);
        drogaRepository.save(ibuprofeno);
    }

    // ── CREATE

    @Test
    void testCreate() {
        //Arrange
        Droga penicilina = new Droga(null, "Penicilina", 4.0f, 9.0f, 80, 0);

        //Act
        Droga guardada = drogaRepository.save(penicilina);

        //Assert
        assertNotNull(guardada.getId());
        assertEquals("Penicilina", guardada.getNombre());
        assertEquals(9.0f, guardada.getPrecioVenta());
        assertEquals(80, guardada.getUnidadesDisponibles());
    }

    // ── READ ALL 

    @Test
    void testFindAll() {
        //Act
        List<Droga> drogas = drogaRepository.findAll();

        //Assert
        assertEquals(2, drogas.size());
    }

    // ── READ BY ID

    @Test
    void testFindById() {
        //Arrange
        Droga existente = drogaRepository.findAll().get(0);

        //Act
        Optional<Droga> resultado = drogaRepository.findById(existente.getId());

        //Assert
        assertTrue(resultado.isPresent());
        assertEquals(existente.getNombre(), resultado.get().getNombre());
    }

    // ── UPDATE 
    @Test
    void testUpdate() {
        //Arrange
        Droga droga = drogaRepository.findAll().get(0);
        droga.setPrecioVenta(20.0f);
        droga.setUnidadesDisponibles(200);

        //Act
        Droga actualizada = drogaRepository.save(droga);

        //Assert
        assertEquals(20.0f, actualizada.getPrecioVenta());
        assertEquals(200,   actualizada.getUnidadesDisponibles());
    }

    // ── DELETE 

    @Test
    void testDelete() {
        //Arrange
        Droga droga = drogaRepository.findAll().get(0);
        Long id = droga.getId();

        //Act
        drogaRepository.deleteById(id);

        //Assert
        Optional<Droga> eliminada = drogaRepository.findById(id);
        assertFalse(eliminada.isPresent());
        assertEquals(1, drogaRepository.count());
    }

    @Test
    void testFindById_idInexistente_retornaVacio() {
        //Arrange
        Long idInexistente = 999L;

        //Act
        Optional<Droga> resultado = drogaRepository.findById(idInexistente);

        //Assert
        assertFalse(resultado.isPresent());
    }

    @Test
    void testFindAll_cuandoNoHayRegistros_retornaListaVacia() {
        //Arrange
        drogaRepository.deleteAll();

        //Act
        List<Droga> resultado = drogaRepository.findAll();

        //Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testUpdate_modificarUnidadesVendidas_actualizaContador() {
        //Arrange
        Droga droga = drogaRepository.findAll().get(0); 
        int cantidadVendida = 15;

        droga.setUnidadesDisponibles(droga.getUnidadesDisponibles() - cantidadVendida);
        droga.setUnidadesVendidas(droga.getUnidadesVendidas() + cantidadVendida);

        //Act
        Droga actualizada = drogaRepository.save(droga);

        //Assert
        assertEquals(85, actualizada.getUnidadesDisponibles()); // 100 - 15
        assertEquals(15, actualizada.getUnidadesVendidas());    // 0 + 15
    }

    @Test
    void testCreate_precioVentaMenorQueCompra_sePersiste() {
        //Arrange
        Droga drogaConPrecioCruzado = new Droga(null, "TestDroga", 10.0f, 5.0f, 50, 0);

        //Act
        Droga guardada = drogaRepository.save(drogaConPrecioCruzado);

        //Assert
        assertNotNull(guardada.getId());
        assertEquals(10.0f, guardada.getPrecioCompra());
        assertEquals(5.0f,  guardada.getPrecioVenta());
    }
}