# MediCat - Aplicativo Web para Veterinarias

Proyecto académico desarrollado para la materia **Desarrollo Web**.

<img width="1919" height="866" alt="image" src="https://github.com/user-attachments/assets/72c811c2-9a74-4a83-bf8c-3161ae5bed58" />


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

<img width="1156" height="799" alt="image" src="https://github.com/user-attachments/assets/bd0d660f-1444-411b-aa90-aeae282c035a" />

<img width="1181" height="622" alt="image" src="https://github.com/user-attachments/assets/84e122fd-6be6-4af6-92b3-08e3f1208518" />


### Veterinario

* Accede al portal veterinario
* CRUD de dueños
* CRUD de mascotas
* Registrar tratamientos
* Consultar historial médico
* Visualizar mascotas tratadas

<img width="1413" height="872" alt="image" src="https://github.com/user-attachments/assets/60373a8a-1e8e-4b18-8ad0-474941c5e369" />

<img width="1398" height="810" alt="image" src="https://github.com/user-attachments/assets/5db7a5df-00d7-4f37-be57-1ec88d36229e" />

<img width="1166" height="812" alt="image" src="https://github.com/user-attachments/assets/fc07dd0c-876f-4b4a-a98f-c9cd80c64e9c" />



### Administrador

* Accede al portal administrador
* CRUD de veterinarios
* Visualiza dashboard con KPIs
* Controla estados laborales (activo/inactivo)

<img width="1479" height="566" alt="image" src="https://github.com/user-attachments/assets/2c552a80-5bc3-4cd5-8603-f428be9ca8b1" />

<img width="1506" height="790" alt="image" src="https://github.com/user-attachments/assets/03e893dc-7ed6-428c-9a73-6496c1377e25" />

<img width="1471" height="576" alt="image" src="https://github.com/user-attachments/assets/60db7aee-1a3e-47f7-b1aa-5db251f950be" />


## Arquitectura del Proyecto

El sistema está dividido en:

* Frontend: SPA desarrollada en Angular
* Backend: API REST
* Base de datos: Modelo relacional

##Diagrama de Entidad Relación

<img width="1215" height="556" alt="image" src="https://github.com/user-attachments/assets/25fef992-1305-434e-adf2-5269eee01792" />


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

-> back/demo : ./mvnw spring-boot:run
-> front/FrontMediCat: ng serve -o


## Para las pruebas

Desde la carpeta backend: 
-> cd back/veterinaria/demo
-> ./mvnw test

Desde la carpeta frontend:
-> cd front/FrontMediCat
-> npm install
-> npm test -- --watch=false