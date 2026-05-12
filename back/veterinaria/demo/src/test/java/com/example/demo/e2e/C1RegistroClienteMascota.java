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

    // ── Credenciales del veterinario sembradas por DataLoader.java:112 ─────
    private static final String VET_CORREO    = "elena@vet.com";
    private static final String VET_PASS      = "pass123";
    private static final String VET_PASS_FAIL = "claveIncorrecta";

    // ── Fix B: dataload por corrida — sufijo único para evitar choques
    //          con datos persistidos en el back de producción.
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

    // Repositorios inyectados para garantizar precondiciones del flujo
    // (login del veterinario) sin depender de que el DataLoader corra.
    @Autowired private VeterinarioRepository veterinarioRepository;

    @BeforeEach
    public void init() {
        // Garantizar que el veterinario existe antes de abrir Chrome.
        sembrarVeterinarioE2E();

        WebDriverManager.chromedriver().setup();

        ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.addArguments("--disable-extensions");
        // Flags de estabilidad: previenen crashes esporádicos de Chrome
        // ("session deleted as the browser has closed the connection")
        // en Windows cuando el usuario interactúa con la ventana o cuando
        // el GPU/sandbox del navegador entra en conflicto con el driver.
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--disable-gpu");
        chromeOptions.addArguments("--remote-allow-origins=*");
        chromeOptions.addArguments("--window-size=1920,1080");
        // Descomenta para correr sin ventana visible (recomendado para CI
        // y para evitar que cerremos el navegador sin querer):
        // chromeOptions.addArguments("--headless=new");

        this.driver = new ChromeDriver(chromeOptions);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Test
    public void caso1_registroClienteYMascota() {

        // ── 1) Login del veterinario (1er intento falla, 2do acierta) ──────────
        driver.get(BASE_URL + "/inicio/login");

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Veterinario']"))).click();

        WebElement loginCorreo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@formcontrolname='correo']")));
        WebElement loginPass = driver.findElement(By.xpath("//input[@formcontrolname='contrasenia']"));

        loginCorreo.sendKeys(VET_CORREO);
        loginPass.sendKeys(VET_PASS_FAIL);
        driver.findElement(By.xpath("//button[contains(@class,'btn-login')]")).click();

        WebElement errorLogin = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(@class,'alert-error')]")));
        Assertions.assertThat(errorLogin.isDisplayed()).isTrue();

        loginCorreo = driver.findElement(By.xpath("//input[@formcontrolname='correo']"));
        loginPass = driver.findElement(By.xpath("//input[@formcontrolname='contrasenia']"));
        loginCorreo.clear();
        loginCorreo.sendKeys(VET_CORREO);
        loginPass.clear();
        loginPass.sendKeys(VET_PASS);
        driver.findElement(By.xpath("//button[contains(@class,'btn-login')]")).click();

        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/inicio/login")));

        // ── 2) Registro de cliente (1er intento falla, 2do acierta) ───────────
        driver.get(BASE_URL + "/clientes/nuevo");

        // Primer intento: nombre vacío → error
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nombre")));
        driver.findElement(By.id("apellido")).sendKeys(CLI_APELLIDO);
        driver.findElement(By.id("cedula")).sendKeys(CLI_CEDULA);
        driver.findElement(By.id("correo")).sendKeys(CLI_CORREO);
        driver.findElement(By.id("celular")).sendKeys(CLI_CELULAR);
        driver.findElement(By.id("contrasenia")).sendKeys(CLI_PASS);

        // Bypass de la validación HTML5 (atributo `required`) para que el
        // submit llegue a Angular y dispare el `alert-error` del componente.
        // Sin esto, el navegador bloquea el envío y nunca se ejecuta
        // guardarCliente(), por lo que el test colgaría esperando el error.
        ((JavascriptExecutor) driver).executeScript(
                "var f = document.querySelector('form'); f.setAttribute('novalidate', 'true');");
        driver.findElement(By.xpath("//button[contains(@class,'btn-guardar')]")).click();

        WebElement errorCliente = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(@class,'alert-error')]")));
        Assertions.assertThat(errorCliente.isDisplayed()).isTrue();

        // Segundo intento: corrige el nombre (ya con novalidate desactivado
        // o no, da igual porque el formulario ya es válido)
        WebElement inputNombre = driver.findElement(By.id("nombre"));
        inputNombre.clear();
        inputNombre.sendKeys(CLI_NOMBRE);
        driver.findElement(By.xpath("//button[contains(@class,'btn-guardar')]")).click();

        WebElement okCliente = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(@class,'alert-success')]")));
        Assertions.assertThat(okCliente.isDisplayed()).isTrue();

        // ── 3) Registro de mascota asociada al cliente ─────────────────────────
        driver.get(BASE_URL + "/mascotas/nueva");

        WebElement mascNombre = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nombre")));
        WebElement mascEspecie = driver.findElement(By.id("especie"));
        WebElement mascRaza = driver.findElement(By.id("raza"));
        WebElement mascFecha = driver.findElement(By.id("fechaNacimiento"));
        WebElement mascEdad = driver.findElement(By.id("edad"));
        WebElement mascPeso = driver.findElement(By.id("peso"));

        mascNombre.sendKeys(MASCOTA_NOMBRE);
        mascEspecie.sendKeys("Perro");
        mascRaza.sendKeys("Labrador");

        Select sexo = new Select(driver.findElement(By.id("sexo")));
        sexo.selectByVisibleText("Macho");

        // <input type="date"> exige formato ISO yyyy-MM-dd; usamos JS para
        // setear el valor y disparar los eventos input/change para que Angular
        // actualice el FormControl asociado.
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = '2022-04-15';" +
                "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));" +
                "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                mascFecha);

        mascEdad.sendKeys("3");
        mascEdad.sendKeys(Keys.TAB);
        mascPeso.sendKeys("12.5");
        mascPeso.sendKeys(Keys.TAB);

        Select estado = new Select(driver.findElement(By.id("estado")));
        estado.selectByValue("ACTIVA");

        // ── Fix A: esperar a que el dropdown de clientes esté poblado ANTES
        //          de filtrar. El componente lo llena con findAll() async.
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

        // Verifica que el binding NgModel se actualizó realmente
        Assertions.assertThat(clienteSel.getFirstSelectedOption().getText().trim())
                .containsIgnoringCase(CLI_NOMBRE + " " + CLI_APELLIDO);

        // ── Fix C: asegurarse de que el botón quedó habilitado (es decir,
        //          el formulario es válido) antes de hacer click.
        WebElement btnGuardarMasc = driver.findElement(
                By.xpath("//button[contains(@class,'btn-guardar')]"));
        wait.until(d -> btnGuardarMasc.isEnabled());
        btnGuardarMasc.click();

        WebElement okMascota = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(@class,'alert-success')]")));
        Assertions.assertThat(okMascota.isDisplayed()).isTrue();

        // ── 4) Logout del veterinario y login del cliente con su CÉDULA ────────
        ((JavascriptExecutor) driver).executeScript(
                "window.sessionStorage.clear(); " +
                "document.cookie.split(';').forEach(function(c){" +
                "  document.cookie = c.replace(/^ +/,'').replace(/=.*/, '=;expires=' + new Date().toUTCString() + ';path=/');" +
                "});");
        driver.get(BASE_URL + "/inicio/login");

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Cliente']"))).click();

        WebElement cliCorreo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@formcontrolname='correo']")));
        WebElement cliPass = driver.findElement(By.xpath("//input[@formcontrolname='contrasenia']"));

        cliCorreo.sendKeys(CLI_CEDULA);
        cliPass.sendKeys(CLI_PASS);
        driver.findElement(By.xpath("//button[contains(@class,'btn-login')]")).click();

        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/inicio/login")));

        // ── 5) El cliente ve su mascota en el portal ────────────────────────────
        driver.get(BASE_URL + "/mis-mascotas");

        // ── Fix D: esperar a que se pinten las tarjetas o el estado vacío,
        //          así obtenemos un mensaje claro si Angular tarda o si el
        //          cliente quedó sin mascotas.
        wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//div[contains(@class,'mm-card')]")),
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//section[contains(@class,'mm-empty')]"))));

        List<WebElement> tarjetas = driver.findElements(
                By.xpath("//div[contains(@class,'mm-card')]//h3"));

        Assertions.assertThat(tarjetas)
                .as("El portal del cliente debe mostrar al menos una mascota registrada")
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
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Garantiza que el veterinario "elena@vet.com" exista y esté activo.
     * Idempotente: si el DataLoader ya lo creó, no hace nada.
     */
    private void sembrarVeterinarioE2E() {
        if (veterinarioRepository.findByCorreo(VET_CORREO).isPresent()) return;
        veterinarioRepository.save(new Veterinario(
                "Elena Martínez", "10000001", "3101000001", VET_CORREO,
                "Medicina General", VET_PASS, "default.jpg", "activo"));
    }
}
