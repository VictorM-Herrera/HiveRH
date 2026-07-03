# HiveRH

[English version](README.en.md)

HiveRH es una API REST para la gestion de Recursos Humanos. Permite administrar empleados, cuentas de usuario, roles, estructura organizacional, liquidaciones de sueldo, licencias, vacaciones, suspensiones, denuncias y certificados.

El proyecto esta planteado como un MVP academico: el foco esta en tener reglas de negocio claras, autenticacion con JWT, permisos por rol y endpoints faciles de probar desde Postman o Swagger.

## Documentacion

La documentacion detallada esta en la carpeta `docs`:

- `docs/Requerimiento.md`: alcance funcional y reglas generales del sistema.
- `docs/Informe_Entidades_Endpoints.md`: recorrido completo del sistema, entidades, endpoints y flujo recomendado para Postman/defensa.
- `docs/Conceptual.md`: modelo conceptual del dominio.
- `docs/DER.pdf`: diagrama entidad-relacion.

Este README queda como guia rapida para levantar y entender el proyecto. Para el detalle completo de endpoints conviene ir al informe.

## Requisitos

- JDK 17 o superior.
- MySQL corriendo localmente o en un servidor accesible.
- Maven Wrapper incluido en el repositorio (`mvnw.cmd` / `mvnw`), o Maven instalado.
- Variables de entorno configuradas en el entorno de ejecucion elegido.
- Docker y Docker Compose, opcional para levantar API + MySQL con un solo comando.

## Configuracion

La aplicacion toma su configuracion desde `src/main/resources/application.yaml`. Las variables necesarias son:

| Variable | Descripcion | Ejemplo |
|---|---|---|
| `DB_URL` | URL JDBC de la base MySQL | `jdbc:mysql://localhost:3306/hiverh` |
| `DB_USER` | Usuario de MySQL | `root` |
| `DB_PASSWORD` | Password de MySQL | `admin` |
| `EMAIL_ADDRESS` | Email usado como remitente SMTP | `hiverh.notificaciones@gmail.com` |
| `EMAIL_PASSWORD` | Password de aplicacion del email SMTP | `abcd efgh ijkl mnop` |
| `SECRET` | Clave para firmar JWT | `clave-super-secreta-de-32-bytes-minimo` |
| `EXPIRATION` | Duracion del token en milisegundos | `86400000` |
| `BOOTSTRAP_ADMIN_ENABLED` | Crea un admin inicial al levantar la app | `true` |
| `BOOTSTRAP_ADMIN_USER` | Usuario del admin inicial | `admin` |
| `BOOTSTRAP_ADMIN_EMAIL` | Email del admin inicial | `admin@hiverh.local` |
| `BOOTSTRAP_ADMIN_PASSWORD` | Password del admin inicial, obligatoria si el bootstrap esta activo | `admin123` |

Ejemplo:

```properties
DB_URL=jdbc:mysql://localhost:3306/hiverh
DB_USER=root
DB_PASSWORD=admin
EMAIL_ADDRESS=hiverh.notificaciones@gmail.com
EMAIL_PASSWORD=abcd efgh ijkl mnop
SECRET=clave-super-secreta-de-32-bytes-minimo
EXPIRATION=86400000
BOOTSTRAP_ADMIN_ENABLED=true
BOOTSTRAP_ADMIN_USER=admin
BOOTSTRAP_ADMIN_EMAIL=admin@hiverh.local
BOOTSTRAP_ADMIN_PASSWORD=admin123
```

Para Gmail se recomienda usar una password de aplicacion, no la password personal de la cuenta.

No es obligatorio usar un archivo `.env`. Cada integrante puede configurar estas variables como prefiera: desde IntelliJ IDEA, desde la terminal, desde variables del sistema operativo o desde el entorno que use para ejecutar la aplicacion.

El repositorio incluye `.env.sample` como plantilla. Se puede copiar a `.env` y ajustar valores locales sin subir secretos al repositorio.

En IntelliJ IDEA:

```text
Run/Debug Configurations > Environment variables
```

## Base de datos

HiveRH usa MySQL. Antes de levantar la aplicacion, la base debe existir:

```sql
CREATE DATABASE IF NOT EXISTS hiverh;
```

Hibernate esta configurado con `ddl-auto: update`, por lo que puede crear o actualizar tablas dentro de esa base, pero no crea la base de datos MySQL desde cero.

Para una prueba local desde cero se puede activar el bootstrap de admin con:

```properties
BOOTSTRAP_ADMIN_ENABLED=true
BOOTSTRAP_ADMIN_PASSWORD=admin123
```

Al iniciar la aplicacion se crea una cuenta `ADMIN` si todavia no existe una cuenta con el mismo usuario o email. Este mecanismo esta pensado para demo/desarrollo; no se recomienda activarlo en produccion.

## Ejecucion local

La API queda disponible por defecto en:

```text
http://localhost:8080
```

Con Maven Wrapper:

```bash
./mvnw spring-boot:run
```

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

## Ejecucion con Docker

El repositorio incluye `Dockerfile` y `docker-compose.yml` para levantar MySQL y la API en entorno local/demo:

```bash
docker compose up --build
```

Servicios:

- API: `http://localhost:8080`
- MySQL: `localhost:3307`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

El Compose crea una cuenta demo `ADMIN`:

```text
Usuario: admin
Password: admin123
```

Este entorno esta pensado para desarrollo y portfolio. No usar esas credenciales en produccion.

## Autenticacion

La API usa JWT. Para consumir endpoints protegidos:

1. Ejecutar `POST /api/auth/login`.
2. Copiar el token recibido.
3. Enviar el token en cada request protegido:

```http
Authorization: Bearer <token>
```

Roles principales:

- `ADMIN`: administra todo el sistema.
- `RRHH`: gestiona empleados, licencias, vacaciones, suspensiones, denuncias y liquidaciones.
- `EMPLOYEE`: consulta y opera sobre recursos propios cuando la regla de negocio lo permite.

## Swagger

Con la aplicacion levantada:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Swagger esta liberado para facilitar pruebas y revision de contratos. Swagger no guarda datos por si mismo: ejecuta requests reales contra la API. Por eso, todo lo que se cree desde Swagger queda guardado en la base MySQL configurada en `DB_URL`.

Flujo recomendado para probar desde Swagger:

1. Levantar MySQL y crear la base `hiverh`.
2. Configurar las variables de entorno, incluyendo el admin bootstrap si se quiere una demo rapida.
3. Ejecutar la aplicacion.
4. Entrar a `http://localhost:8080/swagger-ui.html`.
5. Ejecutar `POST /api/auth/login` con el usuario admin.
6. Copiar el token de la respuesta.
7. Presionar `Authorize` y pegar solo el token JWT.

Una vez autorizado, Swagger envia el JWT en los endpoints protegidos.

## Endpoints base

El detalle completo de endpoints esta en `docs/Informe_Entidades_Endpoints.md`. Como referencia rapida, los modulos principales son:

| Modulo | Base path |
|---|---|
| Auth | `/api/auth` |
| Accounts | `/api/accounts` |
| Employees | `/api/employees` |
| Branches | `/api/branches` |
| Departments | `/api/departments` |
| Positions | `/api/positions` |
| Variations | `/api/variations` |
| Payrolls | `/api/payrolls` |
| Licenses | `/api/licenses` |
| Certificates | `/api/certificates` |
| Vacations | `/api/vacations` |
| Complaints | `/api/complaints` |
| Suspensions | `/api/suspensions` |

Los filtros en endpoints `GET` se envian por query params. No hace falta mandar todos los filtros: se puede enviar uno, varios o ninguno.

Ejemplos:

```http
GET /api/employees?dni=43917621&page=0&size=10
GET /api/vacations?accepted=false&fullName=Juan Perez&page=0&size=10
GET /api/payrolls/employee/43917621?startDate=2026-01-01&endDate=2026-06-30
```

## Paginacion

Los endpoints paginados usan los parametros estandar de Spring `Pageable`:

```http
page=0
size=10
sort=startDate,desc
```

`page` empieza en 0. `sort` es opcional y ordena los resultados sin cambiar los filtros aplicados.

Endpoints con paginacion:

| Modulo | Endpoint |
|---|---|
| Employees | `GET /api/employees` |
| Licenses | `GET /api/licenses` |
| Payrolls | `GET /api/payrolls` |
| Vacations | `GET /api/vacations` |

Ejemplos:

```http
GET /api/employees?page=0&size=10
GET /api/licenses?status=PENDING&page=0&size=10&sort=requestDate,desc
GET /api/payrolls?page=0&size=10
GET /api/vacations?dniEmployee=43917621&page=0&size=10
```

## Reglas importantes

- Un empleado no puede consultar liquidaciones de otro empleado.
- RRHH y ADMIN pueden consultar liquidaciones de cualquier empleado.
- Solo RRHH y ADMIN pueden cargar, modificar o borrar liquidaciones.
- No se permite cargar dos liquidaciones para el mismo empleado en el mismo mes.
- El empleado puede eliminar sus propias solicitudes de licencia o vacaciones solo si no fueron aceptadas.
- RRHH no elimina solicitudes de licencia/vacaciones: las gestiona, aprueba o rechaza.
- ADMIN puede administrar todos los recursos.
- Las denuncias solo pueden ser listadas o revisadas por RRHH o ADMIN.

## Errores comunes

- `401 Unauthorized`: falta token o el token no es valido.
- `403 Forbidden`: el usuario esta autenticado, pero no tiene permisos para esa accion.
- `404 Not Found`: el recurso solicitado no existe.
- `415 Unsupported Media Type`: el `Content-Type` no coincide con lo que espera el endpoint. Por ejemplo, enviar JSON a un endpoint que espera `multipart/form-data`.

## Stack tecnico

- Java
- Spring Boot
- Spring Web MVC
- Spring Security
- JWT con `jjwt`
- Spring Data JPA
- Hibernate
- MySQL
- Bean Validation / Jakarta Validation
- Lombok
- MapStruct
- Springdoc OpenAPI / Swagger UI
- Maven

## Autores

- Gallego Romero Gonzalo N.
- Herrera Victor M.
- Molina Cristian N.
- Romero Rajoy Jose L.
