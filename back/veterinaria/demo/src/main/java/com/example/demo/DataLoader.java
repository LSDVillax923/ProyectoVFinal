package com.example.demo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.demo.entities.Admin;
import com.example.demo.entities.Cita;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.Droga;
import com.example.demo.entities.Mascota;
import com.example.demo.entities.Tratamiento;
import com.example.demo.entities.TratamientoDroga;
import com.example.demo.entities.Veterinario;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.CitaRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.DrogaRepository;
import com.example.demo.repository.MascotaRepository;
import com.example.demo.repository.TratamientoDrogaRepository;
import com.example.demo.repository.TratamientoRepository;
import com.example.demo.repository.VeterinarioRepository;
import com.example.demo.util.FechaUtils;

import jakarta.transaction.Transactional;

@Component
@Order(2)
@Transactional
public class DataLoader implements CommandLineRunner {

    @Autowired private AdminRepository adminRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private DrogaRepository drogaRepository;
    @Autowired private MascotaRepository mascotaRepository;
    @Autowired private TratamientoRepository tratamientoRepository;
    @Autowired private TratamientoDrogaRepository tratamientoDrogaRepository;
    @Autowired private VeterinarioRepository veterinarioRepository;
    @Autowired private CitaRepository citaRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // Datos de prueba
    // ─────────────────────────────────────────────────────────────────────────
    private static final String[] NOMBRES = {
        "Andrés", "Camila", "Luis", "Valentina", "Carlos", "Sofía",
        "Juan", "Isabella", "Sergio", "Daniela", "Miguel", "Laura",
        "Alejandro", "María", "Diego", "Paula", "Felipe", "Ana",
        "Sebastián", "Natalia"
    };

    private static final String[] APELLIDOS = {
        "García", "Rodríguez", "López", "Martínez", "González",
        "Pérez", "Sánchez", "Ramírez", "Torres", "Flores",
        "Rivera", "Gómez", "Díaz", "Vargas", "Morales",
        "Ortiz", "Reyes", "Herrera", "Medina", "Castro"
    };

    private static final String[] NOMBRES_MASCOTAS = {
        "Max", "Bella", "Luna", "Rocky", "Coco", "Mia", "Zeus", "Lola",
        "Thor", "Nala", "Rex", "Daisy", "Toby", "Molly", "Duke", "Sadie",
        "Charlie", "Maggie", "Buddy", "Sophie", "Jack", "Lily", "Bear",
        "Rosie", "Cooper", "Zoe", "Oliver", "Stella", "Tucker", "Gracie"
    };

    private static final String[] ESPECIES = {"Perro", "Gato"};
    private static final String[] RAZAS_PERRO = {"Labrador", "Bulldog", "Poodle", "Beagle", "Chihuahua", "Golden Retriever"};
    private static final String[] RAZAS_GATO = {"Persa", "Siamés", "Maine Coon", "Ragdoll", "Bengalí", "Esfinge"};
    private static final String[] SEXOS = {"Macho", "Hembra"};

    private static final String[] DIAGNOSTICOS = {
        "Vacunación anual", "Control de peso", "Infección de oído", "Problema dental",
        "Revisión general", "Problema de piel", "Cojea de pata trasera", "Vómitos frecuentes"
    };

    private static final String[] OBSERVACIONES_TRAT = {
        "Evolución favorable", "Requiere seguimiento en 15 días", "Cambiar dieta",
        "Aplicar pomada cada 12h", "Reposo absoluto", "Volver en una semana"
    };

    private static final String[] MOTIVOS_CITA = {
        "Consulta general", "Vacunación", "Peluquería", "Cirugía programada",
        "Revisión post-tratamiento", "Urgencia", "Desparasitación"
    };

    // ─────────────────────────────────────────────────────────────────────────
    // Runner
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void run(String... args) throws Exception {
        // Idempotencia: si ya hay clientes, no carga datos nuevamente
        if (clienteRepository.count() > 0) {
            System.out.println("DataLoader: Datos ya existen. No se cargan datos de prueba.");
            return;
        }

        Random rnd = new Random(42);

        // ─── 1. Admins ──────────────────────────────────────────────────────────
        adminRepository.save(new Admin(null, "Carlos Admin", "admin1@vet.com", "admin123"));
        adminRepository.save(new Admin(null, "Fernanda Admin", "admin2@vet.com", "admin123"));
        adminRepository.save(new Admin(null, "Ricardo Admin", "admin3@vet.com", "admin123"));
        System.out.println("DataLoader: 3 admins generados.");

        // ─── 2. Veterinarios ────────────────────────────────────────────────────
        veterinarioRepository.save(new Veterinario("Elena Martínez", "10000001", "3101000001", "elena@vet.com",
                "Medicina General", "pass123", "default.jpg", "activo"));
        veterinarioRepository.save(new Veterinario("Ricardo Sánchez", "10000002", "3101000002", "ricardo@vet.com",
                "Cirugía", "pass123", "default.jpg", "activo"));
        veterinarioRepository.save(new Veterinario("Carla Gómez", "10000003", "3101000003", "carla@vet.com",
                "Dermatología", "pass123", "default.jpg", "activo"));
        veterinarioRepository.save(new Veterinario("Julián Ospina", "10000004", "3101000004", "julian@vet.com",
                "Oftalmología", "pass123", "default.jpg", "activo"));
        veterinarioRepository.save(new Veterinario("Marcela Rueda", "10000005", "3101000005", "marcela@vet.com",
                "Odontología", "pass123", "default.jpg", "inactivo"));
        System.out.println("DataLoader: 5 veterinarios generados.");

        // ─── 3. Drogas ──────────────────────────────────────────────────────────
        System.out.println("DataLoader: Drogas ya cargadas por Seed desde Excel.");

        // ─── 4. Clientes ────────────────────────────────────────────────────────
        for (int i = 1; i <= 30; i++) {
            String nombre = NOMBRES[rnd.nextInt(NOMBRES.length)];
            String apellido = APELLIDOS[rnd.nextInt(APELLIDOS.length)];
            String correo = nombre.toLowerCase() + i + "@email.com";
            String celular = "31" + String.format("%08d", rnd.nextInt(100000000));
            clienteRepository.save(new Cliente(nombre, apellido, correo, "pass" + i, celular));
        }
        System.out.println("DataLoader: 30 clientes generados.");

        List<Cliente> clientes = clienteRepository.findAll();

        // ─── 5. Mascotas ────────────────────────────────────────────────────────
    for (int i = 1; i <= 60; i++) {
    String especie = ESPECIES[rnd.nextInt(ESPECIES.length)];
    String raza = especie.equals("Perro")
            ? RAZAS_PERRO[rnd.nextInt(RAZAS_PERRO.length)]
            : RAZAS_GATO[rnd.nextInt(RAZAS_GATO.length)];
    String nombre = NOMBRES_MASCOTAS[rnd.nextInt(NOMBRES_MASCOTAS.length)] + i;
    String sexo = SEXOS[rnd.nextInt(SEXOS.length)];
    
    // Generar fecha de nacimiento aleatoria (entre 1 y 15 años atrás)
    LocalDate fechaNacimiento = LocalDate.now()
            .minusYears(rnd.nextInt(15) + 1)
            .minusMonths(rnd.nextInt(12))
            .minusDays(rnd.nextInt(28));
    
    // ✅ Calcular edad usando la utilidad
    int edad = FechaUtils.calcularEdad(fechaNacimiento);
    
    double peso = Math.round((rnd.nextDouble() * 29 + 1) * 10.0) / 10.0;
    Mascota.EstadoMascota estado = Mascota.EstadoMascota.values()
            [rnd.nextInt(Mascota.EstadoMascota.values().length)];
    String enfermedad = rnd.nextBoolean() ? "Ninguna" : DIAGNOSTICOS[rnd.nextInt(DIAGNOSTICOS.length)];

    Cliente cliente = clientes.get(rnd.nextInt(clientes.size()));

    Mascota mascota = new Mascota(
            nombre, especie, raza, sexo, fechaNacimiento,
            edad, peso, "default-pet.png", estado,
            enfermedad, "Observación de mascota #" + i, cliente);
    
    mascotaRepository.save(mascota);
}
System.out.println("DataLoader: 60 mascotas generadas.");

        List<Mascota> mascotas = mascotaRepository.findAll();
        List<Veterinario> veterinarios = veterinarioRepository.findAll();

        // ─── 6. Tratamientos ────────────────────────────────────────────────────
        for (int i = 1; i <= 40; i++) {
            Mascota mascota = mascotas.get(rnd.nextInt(mascotas.size()));
            Veterinario veterinario = veterinarios.stream()
                    .filter(v -> "activo".equalsIgnoreCase(v.getEstado()))
                    .findAny()
                    .orElse(veterinarios.get(0));

            String diagnostico = DIAGNOSTICOS[rnd.nextInt(DIAGNOSTICOS.length)];
            String observaciones = OBSERVACIONES_TRAT[rnd.nextInt(OBSERVACIONES_TRAT.length)];
            LocalDate fecha = LocalDate.now().minusDays(rnd.nextInt(60)).plusDays(rnd.nextInt(30));
            Tratamiento.EstadoTratamiento estado = Tratamiento.EstadoTratamiento.values()
                    [rnd.nextInt(Tratamiento.EstadoTratamiento.values().length)];

            tratamientoRepository.save(new Tratamiento(diagnostico, observaciones, fecha, estado, mascota, veterinario));
        }
        System.out.println("DataLoader: 40 tratamientos generados.");

        List<Tratamiento> tratamientos = tratamientoRepository.findAll();
// ─── 7. TratamientoDroga ────────────────────────────────────────────────
List<Droga> drogas = drogaRepository.findAll();

if (drogas.isEmpty()) {
    System.out.println("DataLoader: Sin drogas, se omite TratamientoDroga.");
} else {
    for (int i = 0; i < 25; i++) {
        Tratamiento tratamiento = tratamientos.get(rnd.nextInt(tratamientos.size()));
        Droga droga = drogas.get(rnd.nextInt(drogas.size()));
        int cantidad = rnd.nextInt(3) + 1;

        tratamientoDrogaRepository.save(new TratamientoDroga(tratamiento, droga, cantidad));

        droga.setUnidadesVendidas(droga.getUnidadesVendidas() + cantidad);
        droga.setUnidadesDisponibles(Math.max(0, droga.getUnidadesDisponibles() - cantidad));
        drogaRepository.save(droga);
    }
    System.out.println("DataLoader: 25 registros en TratamientoDroga generados.");
}
        // ─── 8. Citas ───────────────────────────────────────────────────────────
        for (int i = 1; i <= 30; i++) {
            Cliente cliente = clientes.get(rnd.nextInt(clientes.size()));
            // Buscar una mascota que pertenezca a ese cliente
            List<Mascota> mascotasDelCliente = mascotaRepository.findByCliente_Id(cliente.getId());
            if (mascotasDelCliente.isEmpty()) continue;

            Mascota mascota = mascotasDelCliente.get(rnd.nextInt(mascotasDelCliente.size()));
            Veterinario veterinario = veterinarios.stream()
                    .filter(v -> "activo".equalsIgnoreCase(v.getEstado()))
                    .findAny()
                    .orElse(veterinarios.get(0));

            // Fecha futura o pasada según el estado
            boolean esFutura = rnd.nextBoolean();
            LocalDate fechaBase = esFutura ? LocalDate.now().plusDays(rnd.nextInt(30) + 1)
                                           : LocalDate.now().minusDays(rnd.nextInt(30) + 1);
            LocalTime horaInicio = LocalTime.of(8 + rnd.nextInt(9), rnd.nextInt(4) * 15); // 8:00 a 17:00, intervalos de 15 min
            LocalDateTime fechaInicio = LocalDateTime.of(fechaBase, horaInicio);
            LocalDateTime fechaFin = fechaInicio.plusMinutes(30 + rnd.nextInt(3) * 30); // 30, 60 o 90 min

            String motivo = MOTIVOS_CITA[rnd.nextInt(MOTIVOS_CITA.length)];
            Cita.EstadoCita estado;
            if (!esFutura) {
                estado = rnd.nextBoolean() ? Cita.EstadoCita.REALIZADA : Cita.EstadoCita.CANCELADA;
            } else {
                estado = Cita.EstadoCita.PENDIENTE;
            }

            // Verificar solapamiento simple para no saturar el DataLoader
            List<Cita> solapadas = citaRepository.findCitasSolapadas(veterinario.getId(), fechaInicio, fechaFin);
            if (!solapadas.isEmpty()) {
                continue; // Saltamos esta cita para evitar conflictos
            }

            citaRepository.save(new Cita(fechaInicio, fechaFin, motivo, estado, cliente, mascota, veterinario));
        }
        System.out.println("DataLoader: Citas generadas (aproximadamente 20-30).");

        System.out.println("DataLoader: Carga de datos de prueba completada exitosamente.");
    }
}