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

    /**
     * Login de cliente. El identificador puede ser su correo o su cédula:
     * si contiene '@' se interpreta como correo, en caso contrario como cédula.
     */
    Cliente login(String identificador, String contrasenia);

    /** Busca clientes por filtros */
    List<Cliente> buscarPorFiltros(String query);

    /** KPI dashboard: total de clientes registrados */
    long contar();
}