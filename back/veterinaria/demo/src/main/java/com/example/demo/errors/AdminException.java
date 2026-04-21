package com.example.demo.errors;

public class AdminException extends RuntimeException {

    public AdminException(String id) {
        super("Admin no encontrado con id: " + id);
    }
}
