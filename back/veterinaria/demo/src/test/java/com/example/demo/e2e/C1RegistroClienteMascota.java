package com.example.demo.e2e;

import java.time.Duration;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.entities.Veterinario;
import com.example.demo.repository.VeterinarioRepository;

import io.github.bonigarcia.wdm.WebDriverManager;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class C1RegistroClienteMascota {

    private final String BASE_URL = "http://localhost:4200";

    // ── Credenciales del veterinario sembradas por DataLoader.java:112 ──────
    private static final String VET_CORREO    = "elena@vet.com";
    private static final String VET_PASS      = "pass123";
    private static final String VET_PASS_FAIL = "claveIncorrecta";

    // ── Sufijo único por corrida para evitar colisión con datos previos ──────
    private final String SUFIJO     = String.valueOf(System.currentTimeMillis());
    private final String CLI_CEDULA = "1098" + SUFIJO.substring(SUFIJO.length() - 6); // 10 dígitos
    private final String CLI_CORREO = "mariana." + SUFIJO + "@email.com";
    private final String CLI_PASS   = "cliente123";
    private final String CLI_NOMBRE   = "Mariana" + SUFIJO.substring(SUFIJO.length() - 4);
    private final String CLI_APELLIDO = "Pérez";
    private final String CLI_CELULAR  = "3001234567";

    private final String MASCOTA_NOMBRE = "Firulais" + SUFIJO.substring(SUFIJO.length() - 4);

    private WebDriver driver;
    private WebDriverWait wait;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @BeforeEach
    public void init() {
        // Garantizar que el veterinario existe antes de abrir el navegador
        sembrarVeterinarioE2E();

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
    public void caso1_registroClienteYMascota() {

        // ── PASO 1: Login del veterinario — primer intento falla ──────────────
        driver.get(BASE_URL + "/inicio/login");

        // Seleccionar modo Veterinario
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Veterinario']"))).click();

        WebElement loginCorreo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@formcontrolname='correo']")));
        WebElement loginPass = driver.findElement(
                By.xpath("//input[@formcontrolname='contrasenia']"));

        loginCorreo.sendKeys(VET_CORREO);
        loginPass.sendKeys(VET_PASS_FAIL);
        driver.findElement(By.xpath("//button[contains(@class,'btn-login')]")).click();

        // Debe aparecer mensaje de error
        WebElement errorLogin = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(@class,'alert-error')]")));
        Assertions.assertThat(errorLogin.isDisplayed())
                .as("Paso 1: debe mostrarse error tras credenciales incorrectas")
                .isTrue();

        // ── PASO 2: Segundo intento con credenciales correctas ────────────────
        loginCorreo = driver.findElement(By.xpath("//input[@formcontrolname='correo']"));
        loginPass   = driver.findElement(By.xpath("//input[@formcontrolname='contrasenia']"));
        loginCorreo.clear();
        loginCorreo.sendKeys(VET_CORREO);
        loginPass.clear();
        loginPass.sendKeys(VET_PASS);
        driver.findElement(By.xpath("//button[contains(@class,'btn-login')]")).click();

        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("/inicio/login")));

        // ── PASO 3: Registro de cliente — primer intento con nombre vacío ─────
        driver.get(BASE_URL + "/clientes/nuevo");

        // Llenar todos los campos menos el nombre
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nombre")));
        driver.findElement(By.id("apellido")).sendKeys(CLI_APELLIDO);
        driver.findElement(By.id("cedula")).sendKeys(CLI_CEDULA);
        driver.findElement(By.id("correo")).sendKeys(CLI_CORREO);
        driver.findElement(By.id("celular")).sendKeys(CLI_CELULAR);
        driver.findElement(By.id("contrasenia")).sendKeys(CLI_PASS);

        // Desactivar validación HTML5 nativa para que llegue hasta el handler Angular
        ((JavascriptExecutor) driver).executeScript(
                "document.querySelector('form').setAttribute('novalidate','true');");
        driver.findElement(By.xpath("//button[contains(@class,'btn-guardar')]")).click();

        WebElement errorCliente = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(@class,'alert-error')]")));
        Assertions.assertThat(errorCliente.isDisplayed())
                .as("Paso 3: debe mostrarse error cuando el nombre está vacío")
                .isTrue();

        // ── PASO 4: Corregir nombre y registrar cliente exitosamente ──────────
        WebElement inputNombre = driver.findElement(By.id("nombre"));
        inputNombre.clear();
        inputNombre.sendKeys(CLI_NOMBRE);
        driver.findElement(By.xpath("//button[contains(@class,'btn-guardar')]")).click();

        WebElement okCliente = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(@class,'alert-success')]")));
        Assertions.assertThat(okCliente.isDisplayed())
                .as("Paso 4: el cliente debe quedar registrado correctamente")
                .isTrue();

        // ── PASO 5: Registro de mascota asociada al cliente ───────────────────
        driver.get(BASE_URL + "/mascotas/nueva");

        WebElement mascNombre = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nombre")));
        mascNombre.sendKeys(MASCOTA_NOMBRE);
        driver.findElement(By.id("especie")).sendKeys("Perro");
        driver.findElement(By.id("raza")).sendKeys("Labrador");

        new Select(driver.findElement(By.id("sexo"))).selectByVisibleText("Macho");

        // Fecha de nacimiento via JS (el campo tipo date no acepta sendKeys en todos los SO)
        WebElement mascFecha = driver.findElement(By.id("fechaNacimiento"));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value='2022-04-15';" +
                "arguments[0].dispatchEvent(new Event('input',{bubbles:true}));" +
                "arguments[0].dispatchEvent(new Event('change',{bubbles:true}));",
                mascFecha);

        WebElement mascEdad = driver.findElement(By.id("edad"));
        mascEdad.sendKeys("3");
        mascEdad.sendKeys(Keys.TAB);

        WebElement mascPeso = driver.findElement(By.id("peso"));
        mascPeso.sendKeys("12.5");
        mascPeso.sendKeys(Keys.TAB);

        new Select(driver.findElement(By.id("estado"))).selectByValue("ACTIVA");

        // Esperar a que el dropdown de clientes esté poblado (llamada async)
        By selectorCliente = By.id("clienteId");
        wait.until(d -> new Select(d.findElement(selectorCliente)).getOptions().size() > 1);

        Select clienteSel = new Select(driver.findElement(selectorCliente));
        String nombreEsperado = (CLI_NOMBRE + " " + CLI_APELLIDO).toLowerCase();

        WebElement opcionCliente = clienteSel.getOptions().stream()
                .filter(o -> o.getText().trim().toLowerCase().contains(nombreEsperado))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "El cliente '" + CLI_NOMBRE + " " + CLI_APELLIDO +
                        "' no apareció en el dropdown. Opciones: " +
                        clienteSel.getOptions().stream().map(WebElement::getText).toList()));

        clienteSel.selectByVisibleText(opcionCliente.getText().trim());

        Assertions.assertThat(clienteSel.getFirstSelectedOption().getText().trim())
                .as("El cliente debe quedar seleccionado en el dropdown")
                .containsIgnoringCase(CLI_NOMBRE + " " + CLI_APELLIDO);

        WebElement btnGuardarMasc = driver.findElement(
                By.xpath("//button[contains(@class,'btn-guardar')]"));
        wait.until(d -> btnGuardarMasc.isEnabled());
        btnGuardarMasc.click();

        WebElement okMascota = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(@class,'alert-success')]")));
        Assertions.assertThat(okMascota.isDisplayed())
                .as("Paso 5: la mascota debe registrarse correctamente a la primera")
                .isTrue();

        // ── PASO 6: El veterinario cierra sesión; el cliente inicia sesión ─────
        ((JavascriptExecutor) driver).executeScript(
                "window.sessionStorage.clear();" +
                "document.cookie.split(';').forEach(function(c){" +
                "  document.cookie=c.replace(/^ +/,'').replace(/=.*/,'=;expires='+new Date().toUTCString()+';path=/');});");
        driver.get(BASE_URL + "/inicio/login");

        // Seleccionar modo Cliente
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Cliente']"))).click();

        WebElement cliCorreo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@formcontrolname='correo']")));
        WebElement cliPass = driver.findElement(
                By.xpath("//input[@formcontrolname='contrasenia']"));

        // El cliente entra con su CÉDULA (el portal acepta cédula o correo)
        cliCorreo.sendKeys(CLI_CEDULA);
        cliPass.sendKeys(CLI_PASS);
        driver.findElement(By.xpath("//button[contains(@class,'btn-login')]")).click();

        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("/inicio/login")));

        // ── PASO 7: El cliente ve su mascota en el portal ─────────────────────
        driver.get(BASE_URL + "/mis-mascotas");

        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[contains(@class,'mm-card')]")),
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//section[contains(@class,'mm-empty')]"))));

        List<WebElement> tarjetas = driver.findElements(
                By.xpath("//div[contains(@class,'mm-card')]//h3"));

        Assertions.assertThat(tarjetas)
                .as("El portal del cliente debe mostrar al menos una mascota")
                .isNotEmpty();

        boolean apareceMascota = tarjetas.stream()
                .anyMatch(t -> t.getText() != null
                        && t.getText().toLowerCase().contains(MASCOTA_NOMBRE.toLowerCase()));

        Assertions.assertThat(apareceMascota)
                .as("La mascota '" + MASCOTA_NOMBRE + "' debe aparecer entre las tarjetas. Vistas: "
                        + tarjetas.stream().map(WebElement::getText).toList())
                .isTrue();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    /**
     * Garantiza que el veterinario "elena@vet.com" exista y esté activo.
     * Idempotente: si el DataLoader ya lo creó, no hace nada.
     */
    private void sembrarVeterinarioE2E() {
        if (veterinarioRepository.findByCorreo(VET_CORREO).isPresent()) return;
        veterinarioRepository.save(new Veterinario(
                "Elena Martínez", "10000001", "3101000001", VET_CORREO,
                "Medicina General", VET_PASS, "activo"));
    }
}