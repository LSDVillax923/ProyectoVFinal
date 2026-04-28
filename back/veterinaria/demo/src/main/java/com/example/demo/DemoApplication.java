package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de arranque de la aplicación Spring Boot.
 */
@SpringBootApplication
public class DemoApplication {

    /** Método principal que inicia la aplicación */
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}