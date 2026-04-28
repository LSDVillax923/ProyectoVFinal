package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.Droga;
import com.example.demo.errors.DrogaException;
import com.example.demo.repository.DrogaRepository;

/**
 * Implementación del servicio de drogas.
 */
@Service
@Transactional
public class DrogaServiceImpl implements DrogaService {

    @Autowired
    private DrogaRepository drogaRepository;

    /** Busca una droga por ID */
    @Override
    public Droga findById(Long id) {
        return drogaRepository.findById(id)
                .orElseThrow(() -> new DrogaException("Droga no encontrada con ID: " + id));
    }

    /** Lista todas las drogas */
    @Override
    public List<Droga> findAll() {
        return drogaRepository.findAll();
    }

    /** Lista drogas disponibles */
    @Override
    public List<Droga> findDisponibles() {
        return drogaRepository.findByUnidadesDisponiblesGreaterThan(0);
    }

    /** Guarda una droga con validaciones */
    @Override
    public Droga save(Droga droga) {
        validarDroga(droga);
        return drogaRepository.save(droga);
    }

    /** Actualiza una droga */
    @Override
    public Droga update(Long id, Droga drogaDetails) {
        Droga existing = findById(id);
        existing.setNombre(drogaDetails.getNombre());
        existing.setPrecioCompra(drogaDetails.getPrecioCompra());
        existing.setPrecioVenta(drogaDetails.getPrecioVenta());
        existing.setUnidadesDisponibles(drogaDetails.getUnidadesDisponibles());
        return drogaRepository.save(existing);
    }

    /** Elimina una droga */
    @Override
    public void delete(Long id) {
        Droga droga = findById(id);
        drogaRepository.delete(droga);
    }

    /** Descuenta unidades del stock */
    @Override
    public void descontarUnidades(Long id, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad inválida");
        }
        int filasAfectadas = drogaRepository.descontarStockSiDisponible(id, cantidad);
        if (filasAfectadas == 0) {
            Droga droga = findById(id);
            throw new IllegalArgumentException("Stock insuficiente para " + droga.getNombre());
        }
    }

    /** Validaciones básicas */
    private void validarDroga(Droga d) {
        if (d.getNombre() == null || d.getNombre().isBlank()) {
            throw new IllegalArgumentException("Nombre obligatorio");
        }
        if (d.getPrecioCompra() <= 0) throw new IllegalArgumentException("Precio compra > 0");
        if (d.getPrecioVenta() <= 0) throw new IllegalArgumentException("Precio venta > 0");
    }
}