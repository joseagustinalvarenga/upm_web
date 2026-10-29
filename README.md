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

### Cuenta adicional de administrador

La migración `V19__add_recovery_admin.sql` crea `admin_upm` con rol ADMIN y acceso habilitado al iniciar la nueva versión. La contraseña se entrega por separado; el repositorio contiene únicamente su hash BCrypt. La cuenta original se conserva. No editar migraciones ya aplicadas para cambiar contraseñas.

Para verificar el ingreso de esta cuenta sin guardar su contraseña en los tests:

```powershell
mvn test "-Drecovery.admin.password=<contraseña entregada>"
```

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
# upm_web

## Especial de Navidad

- Administración: `/admin/navidad` (requiere rol ADMIN).
- Alumnos: `/navidad`; galería: `/navidad/galeria`.
- La migración `V20__christmas_special.sql` crea la galería y deja el especial **desactivado**. No hay activación por fecha.
- En el panel, cargar los videos, definir un código compartido de 6 a 64 caracteres y marcar «Habilitar el especial para alumnos» solamente cuando corresponda. Dejar el campo de código vacío conserva el actual.
- Cada video admite título, descripción, categoría libre, enlace HTTPS de YouTube/Instagram/TikTok/Facebook, portada opcional de hasta 5 MB, orden y visibilidad. Las portadas se comprimen y se guardan en la base de datos. No se incluyen videos de ejemplo.
- Guardar la configuración revoca todas las sesiones de alumnos anteriores. Apagar el especial también bloquea enlaces directos y oculta la entrada del menú. La autorización dura como máximo 2 horas; salir no cierra la sesión administrativa.
- El código se almacena como hash BCrypt. Tras 5 fallos por dirección remota hay un bloqueo de 15 minutos. El contador reside en memoria por instancia y se reinicia al reiniciar el servidor; en despliegues con varias instancias se debe usar un contador compartido y sesiones compartidas. No se confía en encabezados de IP enviados por el cliente.
- Los formularios de esta sección incluyen protección CSRF. Se conserva el comportamiento del resto de los formularios existentes.
- El acceso limita la galería, no la privacidad de videos alojados en redes externas. Los enlaces abren en otra pestaña y pueden requerir una cuenta en la red social.
- La migración se aplica al desplegar la aplicación con Flyway y PostgreSQL. El desarrollo local con perfil `local` usa H2 y creación de tablas por Hibernate.

Pruebas: `mvn test`. Si el entorno Windows restringe la conexión dinámica del agente Mockito, ejecutar Maven con `-DargLine=-javaagent:<ruta local a byte-buddy-agent.jar>`.
