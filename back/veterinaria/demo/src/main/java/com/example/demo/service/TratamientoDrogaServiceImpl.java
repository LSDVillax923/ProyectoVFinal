package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.dto.MedicamentoCantidadDto;
import com.example.demo.entities.Droga;
import com.example.demo.entities.Tratamiento;
import com.example.demo.entities.TratamientoDroga;
import com.example.demo.repository.DrogaRepository;
import com.example.demo.repository.TratamientoDrogaRepository;
import com.example.demo.repository.TratamientoRepository;

@Service
@Transactional
public class TratamientoDrogaServiceImpl implements TratamientoDrogaService {

    @Autowired
    private TratamientoDrogaRepository tdRepository;

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Autowired
    private DrogaRepository drogaRepository;

    @Autowired
    private DrogaService drogaService; // para descontar stock

    @Override
    public TratamientoDroga findById(Long id) {
        return tdRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Línea de tratamiento no encontrada"));
    }

    @Override
    public List<TratamientoDroga> findByTratamientoId(Long tratamientoId) {
        return tdRepository.findByTratamientoId(tratamientoId);
    }

    @Override
    public TratamientoDroga agregarDroga(Long tratamientoId, Long drogaId, int cantidad) {
        Tratamiento tratamiento = tratamientoRepository.findById(tratamientoId)
                .orElseThrow(() -> new IllegalArgumentException("Tratamiento no encontrado"));
        Droga droga = drogaRepository.findById(drogaId)
                .orElseThrow(() -> new IllegalArgumentException("Droga no encontrada"));

        if (cantidad < 1) {
            throw new IllegalArgumentException("Cantidad debe ser al menos 1");
        }
        // Descontar stock
        drogaService.descontarUnidades(drogaId, cantidad);

        TratamientoDroga td = new TratamientoDroga(tratamiento, droga, cantidad);
        return tdRepository.save(td);
    }

    @Override
    public void eliminarDroga(Long id) {
        TratamientoDroga td = findById(id);
        // Si se desea devolver stock, se podría implementar
        tdRepository.delete(td);
    }

    @Override
    public List<MedicamentoCantidadDto> medicamentosEntre(LocalDate inicio, LocalDate fin) {
        return tdRepository.sumarMedicamentosEntre(inicio, fin);
    }
}