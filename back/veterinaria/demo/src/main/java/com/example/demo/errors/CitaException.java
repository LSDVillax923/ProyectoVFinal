package com.example.demo.errors;

public class CitaException extends RuntimeException {
    public CitaException(String message) {
        super(message);
    }
}