package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Clase principal de arranque de la aplicación Spring Boot.
 * @EnableScheduling habilita los jobs anotados con @Scheduled
 * (por ejemplo el RecordatorioCitaScheduler que corre a las 08:00 cada día).
 */
@SpringBootApplication
@EnableScheduling
public class DemoApplication {

    /** Método principal que inicia la aplicación */
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
