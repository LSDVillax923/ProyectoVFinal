package com.example.demo.errors;

public class TratamientoException extends RuntimeException {
    public TratamientoException(String message) {
        super(message);
    }
}