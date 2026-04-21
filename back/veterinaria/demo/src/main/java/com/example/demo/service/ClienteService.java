package com.example.demo.service;

import java.util.List;
import com.example.demo.entities.Cliente;

public interface ClienteService {
    Cliente findById(Long id);
    List<Cliente> findAll();
    Cliente save(Cliente cliente);
    Cliente update(Long id, Cliente clienteDetails);
    void delete(Long id);
    Cliente login(String correo, String contrasenia);
    List<Cliente> buscarPorFiltros(String query);
}