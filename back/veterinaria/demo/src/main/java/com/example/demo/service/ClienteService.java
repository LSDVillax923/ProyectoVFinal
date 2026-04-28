package com.example.demo.service;

import java.util.List;

import com.example.demo.entities.Cliente;

/**
 * Servicio para gestionar clientes.
 */
public interface ClienteService {

    /** Busca un cliente por ID */
    Cliente findById(Long id);

    /** Lista todos los clientes */
    List<Cliente> findAll();

    /** Guarda un cliente */
    Cliente save(Cliente cliente);

    /** Actualiza un cliente */
    Cliente update(Long id, Cliente clienteDetails);

    /** Elimina un cliente */
    void delete(Long id);

    /** Login de cliente */
    Cliente login(String correo, String contrasenia);

    /** Busca clientes por filtros */
    List<Cliente> buscarPorFiltros(String query);
}