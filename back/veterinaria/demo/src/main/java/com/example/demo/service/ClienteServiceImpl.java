package com.example.demo.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.entities.Cliente;
import com.example.demo.errors.ClienteException;
import com.example.demo.repository.ClienteRepository;

@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public Cliente findById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteException("Cliente no encontrado con ID: " + id));
    }

    @Override
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente save(Cliente cliente) {
        validarCliente(cliente);
        if (cliente.getId() == null && clienteRepository.existsByCorreo(cliente.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un cliente con ese correo");
        }
        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente update(Long id, Cliente clienteDetails) {
        Cliente existing = findById(id);
        existing.setNombre(clienteDetails.getNombre());
        existing.setApellido(clienteDetails.getApellido());
        existing.setCorreo(clienteDetails.getCorreo());
        existing.setContrasenia(clienteDetails.getContrasenia());
        existing.setCelular(clienteDetails.getCelular());
        // Las mascotas no se actualizan aquí, se gestionan en MascotaService
        return clienteRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        Cliente cliente = findById(id);
        clienteRepository.delete(cliente);
    }

    @Override
    public Cliente login(String correo, String contrasenia) {
        return clienteRepository.findByCorreo(correo)
                .filter(c -> c.getContrasenia().equals(contrasenia))
                .orElse(null);
    }

    @Override
    public List<Cliente> buscarPorFiltros(String query) {
        if (query == null || query.isBlank()) {
            return findAll();
        }
        return clienteRepository.buscarPorFiltros(query);
    }

    private void validarCliente(Cliente cliente) {
        if (cliente.getNombre() == null || cliente.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (cliente.getApellido() == null || cliente.getApellido().isBlank()) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }
        if (cliente.getCorreo() == null || !cliente.getCorreo().contains("@")) {
            throw new IllegalArgumentException("Correo inválido");
        }
        if (cliente.getCelular() == null || cliente.getCelular().length() < 10) {
            throw new IllegalArgumentException("Celular debe tener al menos 10 dígitos");
        }
    }
}