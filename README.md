# Proyecto Institucional UPM (Spring Boot Demo)

Este proyecto es una aplicación web institucional completa construida con Spring Boot 3 y Thymeleaf.

## Requisitos Previos

- Java 17 (JDK)
- PostgreSQL (Base de datos creada llamada `institutional_db`)

## Configuración

El archivo `src/main/resources/application.yml` está configurado para conectarse a PostgreSQL en `localhost:5432`.
Asegúrate de tener la base de datos creada:

```sql
CREATE DATABASE institutional_db;
```

Ajusta el username y password en `application.yml` si es diferente a `postgres` / `password`.

## Ejecución

Para ejecutar la aplicación usa Maven:

```bash
./mvnw spring-boot:run
```

La aplicación iniciará en `http://localhost:8080`.

## Usuarios

La migración Flyway (`V2__seed_admin.sql`) crea un usuario administrador por defecto:

- **Usuario**: `admin`
- **Contraseña**: `admin1234`

## Estructura

- **Público**:
    - Home: `/`
    - Cursos: `/courses`
    - Noticias: `/news`
    - Contacto: `/contact`
- **Admin**:
    - Login: `/admin/login`
    - Dashboard: `/admin/dashboard`
    - Gestión de Cursos y Noticias (CRUD completo)
    - Bandeja de Mensajes de Contacto

## Características Técnicas

- **Backend**: Spring Boot 3, Spring Data JPA, Spring Security.
- **Frontend**: Thymeleaf con Bootstrap 5 (CDN).
- **Base de Datos**: PostgreSQL con Migraciones Flyway.
- **Seguridad**: Autenticación basada en formularios, BCrypt hashing.
