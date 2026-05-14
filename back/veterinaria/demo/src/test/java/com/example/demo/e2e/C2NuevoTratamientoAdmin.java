package com.example.demo.e2e;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.entities.Admin;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.Mascota;
import com.example.demo.entities.Veterinario;
import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.MascotaRepository;
import com.example.demo.repository.VeterinarioRepository;

import io.github.bonigarcia.wdm.WebDriverManager;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)


public class C2NuevoTratamientoAdmin {

    private final String BASE_URL = "http://localhost:4200";

    // ── Credenciales sembradas por DataLoader.java (líneas 106 y 112) ───────
    private static final String VET_CORREO   = "elena@vet.com";
    private static final String VET_PASS     = "pass123";
    private static final String ADMIN_CORREO = "admin1@vet.com";
    private static final String ADMIN_PASS   = "admin123";

    private static final String MASCOTA_E2E    = "MaxE2E";
    private static final String DIAGNOSTICO_E2E = "Control postoperatorio caso 2";

    private WebDriver driver;
    private WebDriverWait wait;

    @Autowired private ClienteRepository     clienteRepository;
    @Autowired private MascotaRepository     mascotaRepository;
    @Autowired private VeterinarioRepository veterinarioRepository;
    @Autowired private AdminRepository       adminRepository;

    @BeforeEach
    public void init() {
        sembrarVeterinarioE2E();
        sembrarAdminE2E();
        sembrarMascotaE2E();

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-extensions");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--window-size=1920,1080");

        this.driver = new ChromeDriver(options);
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Test
    public void caso2_nuevoTratamientoYValidacionAdmin() {

        // ── PASO 1: Login del veterinario ─────────────────────────────────────
        driver.get(BASE_URL + "/inicio/login");

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Veterinario']"))).click();

        WebElement loginCorreo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@formcontrolname='correo']")));
        WebElement loginPass = driver.findElement(
                By.xpath("//input[@formcontrolname='contrasenia']"));

        loginCorreo.sendKeys(VET_CORREO);
        loginPass.sendKeys(VET_PASS);
        driver.findElement(By.xpath("//button[contains(@class,'btn-login')]")).click();

        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("/inicio/login")));

        // ── PASO 2: Buscar la mascota y abrir formulario de tratamiento ────────
        driver.get(BASE_URL + "/mascotas");

        WebElement buscador = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[contains(@placeholder,'Buscar por nombre')]")));
        buscador.clear();
        buscador.sendKeys(MASCOTA_E2E);

        // El botón "Nuevo tratamiento" de la primera fila
        WebElement btnNuevoTratamiento = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//tbody/tr[1]//button[@title='Nuevo tratamiento']")));
        btnNuevoTratamiento.click();

        // ── PASO 3: Registrar el tratamiento con un medicamento ────────────────
        wait.until(ExpectedConditions.urlContains("/tratamientos/nuevo"));

        WebElement diagnostico = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("diagnostico")));
        diagnostico.sendKeys(DIAGNOSTICO_E2E);

        WebElement observaciones = driver.findElement(By.id("observaciones"));
        observaciones.sendKeys("Evolución estable. Revisión en 7 días.");

        // Agregar un medicamento
        driver.findElement(By.xpath("//button[contains(.,'Agregar medicamento')]")).click();

        // Seleccionar la primera droga disponible en el select
        WebElement selectDroga = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'droga-row')][1]//select[contains(@name,'drogaId_')]")));
        selectDroga.click();
        selectDroga.findElement(By.xpath(".//option[position()>1]")).click();

        // Seleccionar cantidad 1
        WebElement selectCantidad = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'droga-row')][1]//select[contains(@name,'drogaCantidad_')]")));
        selectCantidad.click();
        selectCantidad.findElement(By.xpath(".//option[@value='1' or normalize-space()='1']")).click();

        // Guardar tratamiento
        driver.findElement(By.xpath(
                "//button[contains(@class,'btn-guardar') and contains(.,'Guardar Tratamiento')]")).click();

        WebElement okTratamiento = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(@class,'alert-success') and contains(.,'tratamiento fue registrado')]")));
        Assertions.assertThat(okTratamiento.isDisplayed())
                .as("Paso 3: el tratamiento debe registrarse correctamente")
                .isTrue();

        // ── PASO 4: Verificar que el tratamiento quedó en el listado ──────────
        driver.get(BASE_URL + "/tratamientos");

        WebElement filtro = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[contains(@placeholder,'Buscar por mascota, veterinario o diagnóstico')]")));
        filtro.clear();
        filtro.sendKeys(DIAGNOSTICO_E2E);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tbody/tr[not(contains(@style,'display: none'))]")));

        List<WebElement> coincidencias = driver.findElements(
                By.xpath("//tbody/tr/td[contains(@class,'diagnostico-cell') " +
                         "and contains(.,'" + DIAGNOSTICO_E2E + "')]"));
        Assertions.assertThat(coincidencias.size())
                .as("Paso 4: el tratamiento '" + DIAGNOSTICO_E2E + "' debe aparecer en el listado")
                .isGreaterThan(0);

        // ── PASO 5: Login del administrador ───────────────────────────────────
        driver.get(BASE_URL + "/inicio/login");

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Administrador']"))).click();

        WebElement adminCorreo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@formcontrolname='correo']")));
        WebElement adminPass = driver.findElement(
                By.xpath("//input[@formcontrolname='contrasenia']"));

        adminCorreo.sendKeys(ADMIN_CORREO);
        adminPass.sendKeys(ADMIN_PASS);
        driver.findElement(By.xpath("//button[contains(@class,'btn-login')]")).click();

        // El login del admin redirige a /dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // ── PASO 6: Verificar métricas de medicamentos y ganancias en dashboard
        WebElement cardGanancias = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'stat-card')]" +
                         "[.//div[contains(.,'Ganancias totales')]]" +
                         "//div[contains(@class,'stat-numero')]")));

        WebElement cardTratamientos = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'stat-card')]" +
                         "[.//div[contains(.,'Tratamientos administrados')]]" +
                         "//div[contains(@class,'stat-numero')]")));

        String gananciasTexto    = cardGanancias.getText().trim();
        String tratamientosTexto = cardTratamientos.getText().trim();

        Assertions.assertThat(gananciasTexto)
                .as("Paso 6: el card de Ganancias totales debe mostrar un valor")
                .isNotBlank();

        Assertions.assertThat(tratamientosTexto)
                .as("Paso 6: el card de Tratamientos administrados debe mostrar un valor")
                .isNotBlank();

        // Los valores numéricos deben ser mayores a 0 (hubo al menos un tratamiento con droga)
        double ganancias = parsearNumero(gananciasTexto);
        int tratamientos  = (int) parsearNumero(tratamientosTexto);

        Assertions.assertThat(ganancias)
                .as("Las ganancias totales deben ser > 0 tras registrar el tratamiento con medicamento")
                .isGreaterThan(0);

        Assertions.assertThat(tratamientos)
                .as("Los tratamientos administrados deben ser > 0")
                .isGreaterThan(0);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    // ── Helpers de siembra ────────────────────────────────────────────────────

    /** Garantiza que el veterinario de prueba exista. Idempotente. */
    private void sembrarVeterinarioE2E() {
        if (veterinarioRepository.findByCorreo(VET_CORREO).isPresent()) return;
        veterinarioRepository.save(new Veterinario(
                "Elena Martínez", "10000001", "3101000001", VET_CORREO,
                "Medicina General", VET_PASS, "default.jpg", "activo"));
    }

    /** Garantiza que el admin de prueba exista. Idempotente. */
    private void sembrarAdminE2E() {
        if (adminRepository.findByCorreo(ADMIN_CORREO).isPresent()) return;
        adminRepository.save(new Admin(null, "Carlos Admin", ADMIN_CORREO, ADMIN_PASS));
    }

    /**
     * Garantiza que exista una mascota activa llamada "MaxE2E" asociada a
     * algún cliente. Si no hay clientes disponibles, crea uno propio.
     * Idempotente: si la mascota ya existe no hace nada.
     */
    private void sembrarMascotaE2E() {
        boolean yaExiste = mascotaRepository.findAll().stream()
                .anyMatch(m -> MASCOTA_E2E.equalsIgnoreCase(m.getNombre()));
        if (yaExiste) return;

        Cliente dueno = clienteRepository.findAll().stream().findFirst().orElseGet(() ->
                clienteRepository.save(new Cliente(
                        "Cliente", "E2E", "9999999999",
                        "cliente.e2e@email.com", "passE2E", "3000000000")));

        Mascota mascota = new Mascota(
                MASCOTA_E2E, "Perro", "Labrador", "Macho",
                LocalDate.now().minusYears(3),
                3, 12.5, "default-pet.png",
                Mascota.EstadoMascota.ACTIVA,
                "Ninguna", "Sembrada por el test E2E", dueno);
        mascotaRepository.save(mascota);
    }

    /**
     * Extrae el primer número (entero o decimal) de un texto del dashboard.
     * Ejemplos: "$ 14.000" → 14000.0 | "12" → 12.0 | "$ 0,00" → 0.0
     */
    private double parsearNumero(String texto) {
        if (texto == null || texto.isBlank()) return 0;
        // Eliminar símbolos de moneda, espacios y separadores de miles (punto/coma cuando no son decimales)
        String limpio = texto.replaceAll("[^0-9.,]", "")
                             .replaceAll("\\.", "")   // quitar puntos de miles
                             .replace(",", ".");       // normalizar decimal
        if (limpio.isBlank()) return 0;
        try {
            return Double.parseDouble(limpio);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}