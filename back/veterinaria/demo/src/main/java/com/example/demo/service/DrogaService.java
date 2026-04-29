package com.example.demo.service;

import java.util.List;
import com.example.demo.dto.MedicamentoCantidadDto;
import com.example.demo.entities.Droga;

public interface DrogaService {
    Droga findById(Long id);
    List<Droga> findAll();
    List<Droga> findDisponibles();
    Droga save(Droga droga);
    Droga update(Long id, Droga drogaDetails);
    void delete(Long id);
    void descontarUnidades(Long id, int cantidad);

    /** KPI dashboard: ventas totales acumuladas */
    double ventasTotales();

    /** KPI dashboard: ganancias totales acumuladas */
    double gananciasTotales();

    /** KPI dashboard: top medicamentos más vendidos */
    List<MedicamentoCantidadDto> topMasVendidos(int limite);
}