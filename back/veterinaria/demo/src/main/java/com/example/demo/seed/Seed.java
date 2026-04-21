package com.example.demo.seed;

import java.io.InputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.demo.entities.Droga;
import com.example.demo.repository.DrogaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class Seed implements CommandLineRunner {

    private final DrogaRepository drogaRepository;

    @Override
    public void run(String... args) throws Exception {
        if (drogaRepository.count() > 0) {
            log.info("Seed: Drogas ya existen. Omitiendo.");
            return;
        }
        log.info("Seed: Cargando medicamentos desde Excel...");
        cargarMedicamentosDesdeExcel();
        log.info("Seed: {} medicamentos cargados.", drogaRepository.count());
    }

    private void cargarMedicamentosDesdeExcel() {
        String rutaArchivo = "MEDICAMENTOS_VETERINARIA - Copy.xlsx";

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(rutaArchivo)) {
            if (is == null) {
                log.error("Seed: Archivo no encontrado en resources/{}", rutaArchivo);
                return;
            }

            try (Workbook workbook = new XSSFWorkbook(is)) {
                Sheet sheet = workbook.getSheetAt(0);
                int guardadas = 0;

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null || row.getCell(0) == null) continue;

                    try {
                        Droga droga = new Droga();
                        droga.setNombre(obtenerTexto(row.getCell(0)));
                        droga.setPrecioVenta(obtenerNumero(row.getCell(1)));
                        droga.setPrecioCompra(obtenerNumero(row.getCell(2)));
                        droga.setUnidadesDisponibles((int) obtenerNumero(row.getCell(3)));
                        droga.setUnidadesVendidas((int) obtenerNumero(row.getCell(4)));
                        drogaRepository.save(droga);
                        guardadas++;
                    } catch (Exception e) {
                        log.warn("Seed: Error en fila {}: {}", i + 1, e.getMessage());
                    }
                }
                log.info("Seed: {} medicamentos guardados.", guardadas);
            }
        } catch (Exception e) {
            log.error("Seed: Error leyendo Excel: {}", e.getMessage(), e);
        }
    }

    private String obtenerTexto(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default      -> "";
        };
    }

    private float obtenerNumero(Cell cell) {
        if (cell == null) return 0f;
        return switch (cell.getCellType()) {
            case NUMERIC -> (float) cell.getNumericCellValue();
            case STRING  -> {
                try { yield Float.parseFloat(cell.getStringCellValue().trim()); }
                catch (NumberFormatException e) { yield 0f; }
            }
            default -> 0f;
        };
    }
}