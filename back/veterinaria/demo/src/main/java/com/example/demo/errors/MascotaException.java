package com.example.demo.errors;

public class MascotaException extends RuntimeException {
    
    public MascotaException(Long id) {
        super("Mascota not found with ID: " + id);
    }

        public MascotaException(String mensaje) {
        super(mensaje);
    }
    
}