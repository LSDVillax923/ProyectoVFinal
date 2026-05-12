package com.example.demo.e2e;

import java.time.Duration;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import io.github.bonigarcia.wdm.WebDriverManager;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class C2NuevoTratamientoAdmin {

    private final String BASE_URL = "http://localhost:4200";

    // ── Credenciales sembradas por DataLoader.java (líneas 106 y 112) ──────
    private static final String VET_CORREO   = "elena@vet.com";
    private static final String VET_PASS     = "pass123";
    private static final String ADMIN_CORREO = "admin1@vet.com";
    private static final String ADMIN_PASS   = "admin123";

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
    public void caso2_nuevoTratamientoYValidacionAdmin() {
        // 1) Login veterinario
        driver.get(BASE_URL + "/inicio/login");

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Veterinario']"))).click();

        WebElement loginCorreo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@formcontrolname='correo']")));
        WebElement loginPass = driver.findElement(By.xpath("//input[@formcontrolname='contrasenia']"));

        loginCorreo.sendKeys(VET_CORREO);
        loginPass.sendKeys(VET_PASS);
        driver.findElement(By.xpath("//button[contains(@class,'btn-login')]"))
                .click();

        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/inicio/login")));

        // 2) Buscar mascota y abrir creación de tratamiento desde el listado
        driver.get(BASE_URL + "/mascotas");

        WebElement buscador = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[contains(@placeholder,'Buscar por nombre')]")));
        buscador.clear();
        buscador.sendKeys("Max");

        WebElement botonNuevoTratamiento = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//tbody/tr[1]//button[@title='Nuevo tratamiento']")));
        botonNuevoTratamiento.click();

        // 3) Registrar nuevo tratamiento
        wait.until(ExpectedConditions.urlContains("/tratamientos/nuevo"));

        WebElement diagnostico = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("diagnostico")));
        diagnostico.sendKeys("Control postoperatorio caso 2");

        WebElement observaciones = driver.findElement(By.id("observaciones"));
        observaciones.sendKeys("Evolución estable. Revisión en 7 días.");

        driver.findElement(By.xpath("//button[contains(.,'Agregar medicamento')]"))
                .click();

        WebElement selectDroga = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'droga-row')][1]//select[contains(@name,'drogaId_')]")));
        selectDroga.click();
        selectDroga.findElement(By.xpath(".//option[position()>1]"))
                .click();

        WebElement selectCantidad = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'droga-row')][1]//select[contains(@name,'drogaCantidad_')]")));
        selectCantidad.click();
        selectCantidad.findElement(By.xpath(".//option[@value='1' or normalize-space()='1']"))
                .click();

        driver.findElement(By.xpath("//button[contains(@class,'btn-guardar') and contains(.,'Guardar Tratamiento')]"))
                .click();

        WebElement okTratamiento = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(@class,'alert-success') and contains(.,'tratamiento fue registrado')]")));
        Assertions.assertThat(okTratamiento.isDisplayed()).isTrue();

        // 4) Verificar en listado de tratamientos que quedó guardado
        driver.get(BASE_URL + "/tratamientos");

        WebElement filtro = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[contains(@placeholder,'Buscar por mascota, veterinario o diagnóstico')]")));
        filtro.clear();
        filtro.sendKeys("Control postoperatorio caso 2");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//tbody/tr[not(contains(@style,'display: none'))]")));

        List<WebElement> coincidencias = driver.findElements(
                By.xpath("//tbody/tr/td[contains(@class,'diagnostico-cell') and contains(.,'Control postoperatorio caso 2')]"));
        Assertions.assertThat(coincidencias.size()).isGreaterThan(0);

        // 5) Login administrador y validar métricas de dashboard
        driver.get(BASE_URL + "/inicio/login");

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Administrador']"))).click();

        WebElement adminCorreo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@formcontrolname='correo']")));
        WebElement adminPass = driver.findElement(By.xpath("//input[@formcontrolname='contrasenia']"));

        adminCorreo.sendKeys(ADMIN_CORREO);
        adminPass.sendKeys(ADMIN_PASS);
        driver.findElement(By.xpath("//button[contains(@class,'btn-login')]"))
                .click();

        // El login del admin navega a /dashboard (ver LoginComponent.onSubmit).
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        WebElement cardGanancias = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'stat-card')][.//div[contains(.,'Ganancias totales')]]//div[contains(@class,'stat-numero')]")));
        WebElement cardTratamientos = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'stat-card')][.//div[contains(.,'Tratamientos administrados')]]//div[contains(@class,'stat-numero')]")));

        String gananciasTexto = cardGanancias.getText().trim();
        String tratamientosTexto = cardTratamientos.getText().trim();

        Assertions.assertThat(gananciasTexto).isNotBlank();
        Assertions.assertThat(tratamientosTexto).isNotBlank();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}