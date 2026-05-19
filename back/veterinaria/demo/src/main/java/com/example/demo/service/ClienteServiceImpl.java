package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entities.Cliente;
import com.example.demo.entities.UserEntity;
import com.example.demo.errors.ClienteException;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.UserRepository;

@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /** Buscar cliente por ID */
    @Override
    public Cliente findById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() ->
                        new ClienteException("Cliente no encontrado con ID: " + id));
    }

    /** Listar todos */
    @Override
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    /** Guardar cliente */
    @Override
    public Cliente save(Cliente cliente) {

        validarCliente(cliente);

        if (cliente.getId() == null) {

            if (clienteRepository.existsByCorreo(cliente.getCorreo())) {
                throw new IllegalArgumentException("Correo ya registrado");
            }

            if (cliente.getCedula() != null
                    && !cliente.getCedula().isBlank()
                    && clienteRepository.existsByCedula(cliente.getCedula())) {

                throw new IllegalArgumentException("Cédula ya registrada");
            }
        }

        Cliente guardado = clienteRepository.save(cliente);

        sincronizarUsuarioCliente(guardado);

        return guardado;
    }

    /** Actualizar cliente */
    @Override
    public Cliente update(Long id, Cliente clienteDetails) {

        Cliente existing = findById(id);

        existing.setNombre(clienteDetails.getNombre());
        existing.setApellido(clienteDetails.getApellido());
        existing.setCedula(clienteDetails.getCedula());
        existing.setCorreo(clienteDetails.getCorreo());
        existing.setContrasenia(clienteDetails.getContrasenia());
        existing.setCelular(clienteDetails.getCelular());

        Cliente actualizado = clienteRepository.save(existing);

        sincronizarUsuarioCliente(actualizado);

        return actualizado;
    }

    /** Eliminar cliente */
    @Override
    public void delete(Long id) {

        Cliente cliente = findById(id);

        clienteRepository.delete(cliente);
    }

    /** Login */
    @Override
    public Cliente login(String identificador, String contrasenia) {

        if (identificador == null || identificador.isBlank()) {
            return null;
        }

        Optional<Cliente> encontrado;

        if (identificador.contains("@")) {

            encontrado = clienteRepository.findByCorreo(identificador);

        } else {

            encontrado = clienteRepository.findByCedula(identificador);

            if (encontrado.isEmpty()) {
                encontrado = clienteRepository.findByCorreo(identificador);
            }
        }

        return encontrado
                .filter(c ->
                        c.getContrasenia() != null
                                && c.getContrasenia().equals(contrasenia))
                .orElse(null);
    }

    /** Buscar por filtros */
    @Override
    public List<Cliente> buscarPorFiltros(String query) {

        if (query == null || query.isBlank()) {
            return findAll();
        }

        return clienteRepository.buscarPorFiltros(query);
    }

    /** Contar clientes */
    @Override
    public long contar() {
        return clienteRepository.count();
    }

    /** Validaciones */
    private void validarCliente(Cliente cliente) {

        if (cliente.getNombre() == null || cliente.getNombre().isBlank()) {
            throw new IllegalArgumentException("Nombre obligatorio");
        }

        if (cliente.getApellido() == null || cliente.getApellido().isBlank()) {
            throw new IllegalArgumentException("Apellido obligatorio");
        }

        if (cliente.getCedula() == null || cliente.getCedula().isBlank()) {
            throw new IllegalArgumentException("Cédula obligatoria");
        }

        if (cliente.getCorreo() == null
                || !cliente.getCorreo().contains("@")) {

            throw new IllegalArgumentException("Correo inválido");
        }

        if (cliente.getCelular() == null
                || cliente.getCelular().length() < 10) {

            throw new IllegalArgumentException("Celular inválido");
        }
    }

    /** Sincronizar con usuarios */
    private void sincronizarUsuarioCliente(Cliente cliente) {

        userRepository.findByCorreo(cliente.getCorreo())
                .ifPresentOrElse(user -> {

                    user.setNombre(
                            cliente.getNombre() + " "
                                    + cliente.getApellido());

                    user.setCliente(cliente);

                    user.setRol(UserEntity.RolUsuario.CLIENTE);

                    user.setContrasenia(
                            passwordEncoder.encode(
                                    cliente.getContrasenia()));

                    user.setActivo(true);

                    userRepository.save(user);

                }, () -> userRepository.save(
                        UserEntity.deCliente(
                                cliente,
                                passwordEncoder.encode(
                                        cliente.getContrasenia())
                        )
                ));
    }
}