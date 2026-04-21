package com.example.demo.errors;

public class ClienteException extends RuntimeException 
{
    public ClienteException(Long id) {
        super("Cliente not found with ID: " + id);
    }

        public ClienteException(String mensaje) {
        super(mensaje);
    }
}
