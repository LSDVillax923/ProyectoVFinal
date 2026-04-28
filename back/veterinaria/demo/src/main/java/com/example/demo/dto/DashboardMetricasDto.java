package com.example.demo.dto;

import java.util.List;

public record DashboardMetricasDto(
        long totalClientes,
        long totalMascotas,
        long mascotasEnTratamiento,
        long veterinariosActivos,
        long veterinariosInactivos,
        long tratamientosUltimoMes,
        long citasHoy,
        double ventasTotales,
        double gananciasTotales,
        List<MedicamentoCantidadDto> topMedicamentos,
        List<MedicamentoCantidadDto> medicamentosUltimoMes,
        List<CitaResumenDto> citasProximas
) {
    public record MedicamentoCantidadDto(String nombre, long cantidad) {}

    public record CitaResumenDto(
            String hora,
            String mascota,
            String duenio,
            String tipo,
            String estado
    ) {}
}
