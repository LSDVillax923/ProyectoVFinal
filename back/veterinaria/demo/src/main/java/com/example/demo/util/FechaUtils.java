package com.example.demo.util;

import java.time.LocalDate;
import java.time.Period;

/**
 * Utilidades para manejo de fechas
 */
public class FechaUtils {

    /**
     * Calcula la edad en años a partir de la fecha de nacimiento
     * @param fechaNacimiento Fecha de nacimiento (puede ser null)
     * @return Edad en años, o 0 si la fecha es null o futura
     */
    public static int calcularEdad(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null) {
            return 0;
        }
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            return 0; // No se permiten fechas futuras para nacimiento
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    /**
     * Calcula la edad en meses a partir de la fecha de nacimiento
     * @param fechaNacimiento Fecha de nacimiento
     * @return Edad en meses
     */
    public static int calcularEdadEnMeses(LocalDate fechaNacimiento) {
        if (fechaNacimiento == null || fechaNacimiento.isAfter(LocalDate.now())) {
            return 0;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getMonths();
    }

    /**
     * Valida si una fecha es válida para nacimiento (no futura y no null)
     * @param fechaNacimiento Fecha a validar
     * @return true si es válida
     */
    public static boolean esFechaNacimientoValida(LocalDate fechaNacimiento) {
        return fechaNacimiento != null && !fechaNacimiento.isAfter(LocalDate.now());
    }
}