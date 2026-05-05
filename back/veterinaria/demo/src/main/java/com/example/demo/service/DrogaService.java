package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.MedicamentoCantidadDto;
import com.example.demo.entities.Droga;

/**
 * Servicio para gestionar drogas.
 */
public interface DrogaService {

    /** Busca una droga por ID */
    Droga findById(Long id);

    /** Lista todas las drogas */
    List<Droga> findAll();

    /** Lista drogas disponibles */
    List<Droga> findDisponibles();

    /** Guarda una droga */
    Droga save(Droga droga);

    /** Actualiza una droga */
    Droga update(Long id, Droga drogaDetails);

    /** Elimina una droga */
    void delete(Long id);

    /** Descuenta unidades del stock */
    void descontarUnidades(Long id, int cantidad);

    /** KPI dashboard: ganancias totales acumuladas */
    double gananciasTotales();

    /** KPI dashboard: ventas totales acumuladas */
    double ventasTotales();

    /** KPI dashboard: top medicamentos más vendidos */
    List<MedicamentoCantidadDto> topMasVendidos(int limite);
}