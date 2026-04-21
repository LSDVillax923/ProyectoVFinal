package com.example.demo.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.entities.Droga;
import com.example.demo.errors.DrogaException;
import com.example.demo.repository.DrogaRepository;

@Service
@Transactional
public class DrogaServiceImpl implements DrogaService {

    @Autowired
    private DrogaRepository drogaRepository;

    @Override
    public Droga findById(Long id) {
        return drogaRepository.findById(id)
                .orElseThrow(() -> new DrogaException("Droga no encontrada con ID: " + id));
    }

    @Override
    public List<Droga> findAll() {
        return drogaRepository.findAll();
    }

    @Override
    public List<Droga> findDisponibles() {
        return drogaRepository.findByUnidadesDisponiblesGreaterThan(0);
    }

    @Override
    public Droga save(Droga droga) {
        validarDroga(droga);
        return drogaRepository.save(droga);
    }

    @Override
    public Droga update(Long id, Droga drogaDetails) {
        Droga existing = findById(id);
        existing.setNombre(drogaDetails.getNombre());
        existing.setPrecioCompra(drogaDetails.getPrecioCompra());
        existing.setPrecioVenta(drogaDetails.getPrecioVenta());
        existing.setUnidadesDisponibles(drogaDetails.getUnidadesDisponibles());
        // unidadesVendidas no se actualiza directamente, solo mediante descontarUnidades
        return drogaRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Droga droga = findById(id);
        drogaRepository.delete(droga);
    }

    @Override
    public void descontarUnidades(Long id, int cantidad) {
        Droga droga = findById(id);
        if (droga.getUnidadesDisponibles() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente para " + droga.getNombre());
        }
        droga.setUnidadesDisponibles(droga.getUnidadesDisponibles() - cantidad);
        droga.setUnidadesVendidas(droga.getUnidadesVendidas() + cantidad);
        drogaRepository.save(droga);
    }

    private void validarDroga(Droga d) {
        if (d.getNombre() == null || d.getNombre().isBlank()) {
            throw new IllegalArgumentException("Nombre obligatorio");
        }
        if (d.getPrecioCompra() <= 0) throw new IllegalArgumentException("Precio compra > 0");
        if (d.getPrecioVenta() <= 0) throw new IllegalArgumentException("Precio venta > 0");
    }
}