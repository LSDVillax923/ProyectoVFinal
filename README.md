# MediCat - Aplicativo Web para Veterinarias

Proyecto académico desarrollado para la materia **Desarrollo Web**.

## Descripción del Proyecto

MediCat es una aplicación web diseñada para digitalizar la gestión de una clínica veterinaria especializada en hospitalización de perros y gatos.

El sistema permite:

* Promocionar los servicios de la veterinaria
* Registrar dueños y mascotas
* Gestionar tratamientos médicos
* Permitir a los clientes consultar el estado de sus mascotas
* Proporcionar un portal administrativo con indicadores clave del negocio
* Implementar control de acceso basado en roles

## Objetivo

Desarrollar una solución web completa que permita:

* Optimizar la gestión interna de la clínica
* Llevar control digital de tratamientos y medicamentos
* Brindar acceso diferenciado según el rol del usuario
* Visualizar métricas clave del negocio mediante un dashboard

## Roles del Sistema

El sistema cuenta con tres tipos de usuarios:

### Cliente (Dueño)

* Inicia sesión
* Consulta sus mascotas registradas
* Visualiza detalles y tratamientos
* No puede modificar información

### Veterinario

* Accede al portal veterinario
* CRUD de dueños
* CRUD de mascotas
* Registrar tratamientos
* Consultar historial médico
* Visualizar mascotas tratadas

### Administrador

* Accede al portal administrador
* CRUD de veterinarios
* Visualiza dashboard con KPIs
* Controla estados laborales (activo/inactivo)

## Arquitectura del Proyecto

El sistema está dividido en:

* Frontend: SPA desarrollada en Angular
* Backend: API REST
* Base de datos: Modelo relacional

### Estructura del repositorio

```
/front   → Aplicación Angular (FrontMediCat)
/back    → API REST (lógica de negocio)
/docs    → Documentación (opcional)
```

## Frontend (Angular)

Este proyecto fue generado con Angular CLI versión 21.2.3.

### Servidor de desarrollo

```bash
ng serve
```

Luego abre:

```
http://localhost:4200/
```

La aplicación se recarga automáticamente al modificar archivos.

### Generación de componentes

```bash
ng generate component nombre-componente
```

Para ver todas las opciones:

```bash
ng generate --help
```

### Build

```bash
ng build
```

Los archivos compilados se almacenan en:

```
dist/
```

### Testing

Unit tests:

```bash
ng test
```

End-to-end:

```bash
ng e2e
```

## Reglas de Negocio

* Solo mascotas activas pueden recibir tratamientos
* Si se elimina un dueño, sus mascotas se eliminan en cascada
* Los veterinarios pueden tratar múltiples mascotas y viceversa
* No se utiliza herencia en el modelo de datos
* Se mantiene historial completo de tratamientos

## Control de versiones

Repositorio gestionado con Git, utilizando las siguientes ramas:

* main → rama principal
* develop → integración de desarrollo
* production → despliegue estable

## Estado del Proyecto

En desarrollo como proyecto académico.
