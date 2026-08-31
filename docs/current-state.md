# Current State - HiveRH

Ultima inspeccion: 2026-08-31.

## Que funciona

- Proyecto Spring Boot con Maven Wrapper y Java 17.
- Autenticacion JWT stateless con login publico en `POST /api/auth/login`.
- `POST /api/auth/register` restringido a `ADMIN`.
- Roles actuales: `ADMIN`, `STAFF`, `EMPLOYEE`.
- Cuentas con `AccountStatus` y username/email unicos en entidad.
- Alta de empleados con cuenta `EMPLOYEE` automatica.
- Baja logica de empleados: `EmployeeStatus.TERMINATED`, cierre de asignaciones activas y cuenta asociada inactiva.
- Proteccion de cuenta principal `admin` contra baja por flujo de empleado.
- Estructura organizacional: branches, departments y positions.
- Asignaciones laborales historicas con `EmployeeAssignmentEntity`.
- Work schedules con validacion de solapamiento.
- Work requests con restriccion de duplicados pendientes y registro de revisor.
- Payroll nuevo con periodos, conceptos, detalles, snapshot de sueldo y estados.
- Vacaciones/licencias usan `AbsenceStatus`.
- Certificados PDF asociados a licencias.
- Swagger/OpenAPI disponible en `/swagger-ui.html`.
- Demo cleanup scheduler existe y es opt-in por variables.
- Tests unitarios disponibles para account, employee, security authorization, work schedule, work request y payroll.

## Tests y scripts disponibles

- `src/test/java/com/HiveGroup/HiveRH/HiveRhApplicationTests.java`: carga de contexto Spring.
- `src/test/java/com/HiveGroup/HiveRH/Features/Account/AccountServiceTest.java`.
- `src/test/java/com/HiveGroup/HiveRH/Features/Employee/EmployeeServiceTest.java`.
- `src/test/java/com/HiveGroup/HiveRH/Common/Security/Config/SecurityAuthorizationServiceTest.java`.
- `src/test/java/com/HiveGroup/HiveRH/Features/WorkSchedule/WorkScheduleControllerTest.java`.
- `src/test/java/com/HiveGroup/HiveRH/Features/WorkSchedule/WorkScheduleServiceTest.java`.
- `src/test/java/com/HiveGroup/HiveRH/Features/WorkRequest/WorkRequestServiceTest.java`.
- `src/test/java/com/HiveGroup/HiveRH/Features/Payroll/PayrollServiceTest.java`.
- `HiveRH.http`: smoke test manual para IntelliJ HTTP Client, revisar rutas antes de usar.
- `docs/tp3_v02_work/build_tp3_v02.py`: script auxiliar de documentacion.
- `testdata/certificate-sample.pdf`: archivo de prueba para certificados.

## Incompleto o pendiente

- Pendiente de confirmar como se crea/provisiona el primer `ADMIN` en cada ambiente.
- Pendiente de confirmar estrategia definitiva de schema: `application.yaml` usa `spring.jpa.hibernate.ddl-auto: create`, pero README menciona comportamiento tipo `update`.
- No se vio frontend en el repo.
- No se vio una estrategia formal de migraciones tipo Flyway/Liquibase.
- `docs/Conceptual.md` aparece mencionado en README, pero no existe en el listado actual de `docs`.
- `HiveRH.http` parece desactualizado: contiene rutas singulares como `/api/branch`, `/api/department`, `/api/position` y un register sin token.
- `docs/Informe_Entidades_Endpoints.md` todavia contiene secciones que dicen que register esta disponible para `ADMIN` o `STAFF`; el codigo actual lo deja solo para `ADMIN`.
- La coleccion `docs/HIVEGROUP.postman_collection.json` no fue validada en esta inspeccion.
- No hay tests de integracion web completos con filtro JWT, base de datos y permisos por endpoint.

## Bugs o riesgos visibles

- `ddl-auto: create` puede recrear tablas al iniciar y borrar datos si se usa contra una base con informacion importante.
- El scheduler de demo cleanup hace deletes fisicos sobre tablas operativas cuando esta habilitado; revisar variables antes de activarlo.
- El scheduler depende de nombres de tablas hardcodeados; cualquier rename de entidad/tabla exige actualizarlo.
- Warnings de compilacion observados en corridas previas:
  - Lombok `@Builder` ignora inicializadores en algunas entidades si no usan `@Builder.Default`.
  - MapStruct reporta propiedades destino no mapeadas en algunos mappers.
- Los endpoints de certificados mezclan plural y singular: `POST /api/certificates`, pero `GET/DELETE /api/certificate/{id}` y `GET /api/certificate-info`.
- Algunos textos de docs antiguas hablan de eliminar vacaciones/licencias; confirmar si el comportamiento deseado es delete fisico, cancelacion o baja logica.
- `AuthService` declara un `PasswordEncoder` no final que no se usa en el codigo inspeccionado.

## Proximos pasos recomendados

- Actualizar `README.md`, `README.en.md`, `docs/Postman_Endpoints.md`, `docs/Informe_Entidades_Endpoints.md` y `HiveRH.http` para reflejar register solo `ADMIN` y rutas plurales actuales.
- Definir si `ddl-auto` debe quedar en `create`, `update` o `validate` segun ambiente.
- Agregar seed/migracion para el primer admin o documentar un flujo SQL unico y actualizado.
- Agregar tests de integracion para seguridad real de endpoints: register, role update, `/me`, payroll y work requests.
- Revisar semantica de delete/cancel para vacaciones, licencias y certificados.
- Considerar un perfil de test con H2 o Testcontainers si se quiere probar repositories/controladores sin depender de MySQL local.
- Revisar warnings de Lombok/MapStruct antes de una entrega final.

## Preguntas abiertas

- El primer admin debe nacer por SQL manual, data initializer, migracion o variable de entorno?
- Register se mantiene solo para admins sin empleado asociado o se eliminara por completo mas adelante?
- Vacaciones/licencias deben borrarse fisicamente o pasar a `CANCELLED` para conservar historial?
- El scheduler de demo cleanup se usara solo en Railway/demo o tambien localmente?
- `ddl-auto: create` es intencional para resetear la base durante desarrollo?
- La documentacion final debe considerar `STAFF` como nombre visible para usuarios o mostrarlo como "Recursos Humanos" en textos de negocio?
