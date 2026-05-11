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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class C1RegistroClienteMascota {

    private final String BASE_URL = "http://localhost:4200";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void init() {

        WebDriverManager.chromedriver().setup();

        ChromeOptions chromeOptions = new ChromeOptions();

        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.addArguments("--start-maximized");
        // chromeOptions.addArguments("--headless");

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

        loginCorreo.sendKeys("elena@vet.com");
        loginPass.sendKeys("claveIncorrecta");
        driver.findElement(By.xpath("//button[contains(@class,'btn-login')]")).click();

        WebElement errorLogin = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(@class,'alert-error')]")));
        Assertions.assertThat(errorLogin.isDisplayed()).isTrue();

        loginCorreo = driver.findElement(By.xpath("//input[@formcontrolname='correo']"));
        loginPass = driver.findElement(By.xpath("//input[@formcontrolname='contrasenia']"));
        loginCorreo.clear();
        loginCorreo.sendKeys("elena@vet.com");
        loginPass.clear();
        loginPass.sendKeys("pass123");
        driver.findElement(By.xpath("//button[contains(@class,'btn-login')]")).click();

        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/inicio/login")));

        // ── 2) Registro de cliente ─────────────────────────────────────────────
        driver.get(BASE_URL + "/clientes/nuevo");

        WebElement inputNombre = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nombre")));
        WebElement inputApellido = driver.findElement(By.id("apellido"));
        WebElement inputCedula = driver.findElement(By.id("cedula"));
        WebElement inputCorreo = driver.findElement(By.id("correo"));
        WebElement inputCelular = driver.findElement(By.id("celular"));
        WebElement inputContrasenia = driver.findElement(By.id("contrasenia"));

        inputNombre.sendKeys("Mariana");
        inputApellido.sendKeys("Pérez");
        inputCedula.sendKeys("1098765432");
        inputCorreo.sendKeys("mariana.cliente@email.com");
        inputCelular.sendKeys("3001234567");
        inputContrasenia.sendKeys("cliente123");

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

        mascNombre.sendKeys("Firulais");
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

        Select clienteSel = new Select(driver.findElement(By.id("clienteId")));
        clienteSel.getOptions().stream()
                .filter(o -> o.getText().trim().toLowerCase().contains("mariana pérez"))
                .findFirst()
                .ifPresent(o -> clienteSel.selectByVisibleText(o.getText()));

        driver.findElement(By.xpath("//button[contains(@class,'btn-guardar')]")).click();

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

        cliCorreo.sendKeys("1098765432");
        cliPass.sendKeys("cliente123");
        driver.findElement(By.xpath("//button[contains(@class,'btn-login')]")).click();

        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/inicio/login")));

        // ── 5) El cliente ve su mascota en el portal ────────────────────────────
        driver.get(BASE_URL + "/mis-mascotas");

        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class,'mm-card')]//h3")));

        List<WebElement> tarjetas = driver.findElements(By.xpath("//div[contains(@class,'mm-card')]//h3"));
        boolean apareceFirulais = tarjetas.stream()
                .anyMatch(t -> t.getText() != null && t.getText().toLowerCase().contains("firulais"));

        Assertions.assertThat(apareceFirulais).isTrue();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
