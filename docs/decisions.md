# Decisions - HiveRH

Ultima inspeccion: 2026-08-31.

## Decisiones registradas

### Arquitectura package by feature

- Fecha aproximada: previa a agosto 2026.
- Decision: organizar modulos bajo `Features/<Modulo>` con controller, service, repository, entity, DTO y mapper cuando aplica.
- Motivo: mantener cada dominio acotado y facil de navegar.
- Consecuencia: al modificar una feature, revisar primero su carpeta y luego dependencias en `Common`.

### Roles `ADMIN`, `STAFF`, `EMPLOYEE`

- Fecha aproximada: agosto 2026.
- Decision: usar `STAFF` como rol operativo de Recursos Humanos en lugar de `RRHH`.
- Motivo: nombre mas general y consistente.
- Consecuencia: no reintroducir `RRHH` en enums, seguridad, docs o tests.

### Register solo administrativo

- Fecha aproximada: agosto 2026.
- Decision: `POST /api/auth/register` queda permitido solo para `ADMIN`.
- Motivo: el flujo principal de usuarios debe pasar por alta de empleado y cuenta vinculada; register se conserva para cuentas sin empleado asociado, especialmente admins adicionales.
- Consecuencia: el primer admin debe existir por seed, SQL manual o base preparada. `STAFF` no crea cuentas por register.
- Alternativa descartada: register publico o disponible para `STAFF`.

### Alta de empleado crea cuenta vinculada

- Fecha aproximada: previa/agosto 2026.
- Decision: `POST /api/employees` crea empleado activo, primera asignacion laboral y una cuenta `EMPLOYEE` con usuario/password inicial igual al DNI.
- Motivo: simplificar onboarding y asegurar que el empleado tenga identidad en el sistema.
- Consecuencia: para crear un staff real, primero se crea empleado y luego se actualiza el rol de su cuenta.

### Actualizacion de roles con path `/rol`

- Fecha aproximada: agosto 2026.
- Decision: unificar el endpoint de cambio de rol en `PATCH /api/accounts/{identifier}/rol`.
- Motivo: evitar mezcla entre `/role` y `/rol`.
- Consecuencia: actualizar clientes/docs si aparece `/role`.

### Restricciones de rol

- Fecha aproximada: agosto 2026.
- Decision: solo `ADMIN` puede asignar `ADMIN`; `STAFF` puede asignar roles que no sean `ADMIN`; `EMPLOYEE` no puede cambiar roles.
- Motivo: evitar escalada de privilegios.
- Consecuencia: probar siempre cambios de rol desde service y desde capa web/security.

### Cuenta principal protegida

- Fecha aproximada: agosto 2026.
- Decision: la cuenta con username literal `admin` no debe poder darse de baja desde el flujo de empleado.
- Motivo: conservar acceso administrativo principal.
- Consecuencia: cambios en baja de empleados deben mantener esta proteccion.

### Estados con nombres especificos

- Fecha aproximada: agosto 2026.
- Decision: usar `AccountStatus`, `EmployeeStatus` y `AbsenceStatus` en lugar de enums genericos o viejos.
- Motivo: nombres mas claros por dominio.
- Consecuencia: no reintroducir `StatusEnum` ni `LicenseStatusEnum`.

### Suspensiones y denuncias removidas

- Fecha aproximada: agosto 2026.
- Decision: eliminar modulos de suspensiones y denuncias.
- Motivo: no cerraban en la idea funcional del proyecto.
- Consecuencia: no agregar permisos, schedulers, entidades ni relaciones nuevas para esas features sin confirmacion explicita.

### Asignacion laboral historica

- Fecha aproximada: agosto 2026.
- Decision: mover sucursal/departamento/puesto a `EmployeeAssignmentEntity` con `startDate`, `endDate` y `active`.
- Motivo: conservar historial de cambios laborales.
- Consecuencia: filtros por sucursal/departamento/puesto deben considerar asignacion activa o efectiva para la fecha.

### Payroll mensual por periodo

- Fecha aproximada: agosto 2026.
- Decision: reemplazar liquidacion directa por modelo con `PayrollPeriod`, `PayrollConcept`, `PayrollDetail` y `Payroll`.
- Motivo: representar un flujo mensual con historial, borradores y confirmacion.
- Consecuencia: `Payroll` guarda `baseSalarySnapshot`; el total sale de sueldo snapshot + additions - deductions.
- Alternativa descartada: calcular directamente desde sueldo actual + `Variation`.

### `Variation` reemplazado

- Fecha aproximada: agosto 2026.
- Decision: eliminar `Variation` y usar conceptos reutilizables mas detalles por liquidacion.
- Motivo: separar catalogo de conceptos de la aplicacion concreta en cada payroll.
- Consecuencia: no crear nuevas referencias a `Variation`.

### WorkSchedule y WorkRequest separados

- Fecha aproximada: agosto 2026.
- Decision: separar cronogramas asignados (`WorkSchedule`) de solicitudes puntuales (`WorkRequest`).
- Motivo: distinguir lo que la empresa asigna de lo que el empleado solicita modificar o justificar.
- Consecuencia: al aprobar una solicitud se registra revisor y se genera/ajusta el cronograma.

### Endpoints `/me` por empleado vinculado

- Fecha aproximada: agosto 2026.
- Decision: permitir experiencia self-service a cualquier cuenta autenticada que tenga empleado vinculado.
- Motivo: `STAFF` y `ADMIN` tambien pueden ser empleados si fueron creados por el flujo normal y luego promovidos.
- Consecuencia: no bloquear `/me` solo por tener rol administrativo; validar ownership en services.

### Limpieza demo opt-in

- Fecha aproximada: agosto 2026.
- Decision: agregar scheduler configurable para limpiar datos demo cuando `hiverh.demo-cleanup.enabled=true`.
- Motivo: mantener ambientes publicos de prueba controlados.
- Consecuencia: preservar `admin` por configuracion y revisar tablas antes de cambiar schema.

### Tests versionados

- Fecha aproximada: agosto 2026.
- Decision: los tests bajo `src/test/java` deben versionarse.
- Motivo: documentan reglas de negocio y evitan regresiones.
- Consecuencia: ignorar `target/`, no los tests.
