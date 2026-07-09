package com.HiveGroup.HiveRH.Common.Security.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI hiveRhOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HiveRH API")
                        .description("""
                                API REST para gestionar procesos internos de Recursos Humanos: empleados, cuentas,
                                estructura organizacional, liquidaciones, licencias, vacaciones, suspensiones,
                                denuncias, certificados y variaciones salariales.

                                ### Como probar la API

                                Swagger ejecuta requests reales contra la aplicacion. Los datos que crees, modifiques
                                o elimines desde esta pantalla se guardan en la base MySQL configurada en `DB_URL`.

                                1. Levantar MySQL y crear la base `hiverh`.
                                
                                2. Iniciar la aplicacion con las variables de entorno configuradas.
                                
                                3. Si es una demo desde cero, activar el admin inicial con
                                   `BOOTSTRAP_ADMIN_ENABLED=true` y `BOOTSTRAP_ADMIN_PASSWORD=admin123`.
                                   
                                4. Ejecutar `POST /api/auth/login`.
                                
                                5. Copiar el campo `token` de la respuesta.
                                
                                6. Presionar `Authorize`, pegar solo el JWT y confirmar.

                                ### Roles disponibles

                                - `ADMIN`: administra cuentas, roles, sucursales, departamentos, puestos y recursos generales.
                                
                                - `RRHH`: gestiona empleados, licencias, vacaciones, suspensiones, denuncias y liquidaciones.
                                
                                - `EMPLOYEE`: consulta y opera sobre recursos propios cuando la regla de negocio lo permite.

                                ### Notas de uso

                                - Los endpoints protegidos requieren JWT.
                                - Los filtros de endpoints GET se envian como query params.
                                - No es obligatorio completar todos los filtros; se puede enviar uno, varios o ninguno.
                                """)
                        .version("v1")
                        .contact(new Contact()
                                .name("HiveRH Team")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor local de desarrollo")
                ))
                .tags(List.of(
                        new Tag().name("01 Auth").description("Autenticacion y registro de cuentas para obtener acceso al sistema."),
                        new Tag().name("02 Accounts").description("Operacion sobre la cuenta autenticada y administracion de roles."),
                        new Tag().name("03 Branches").description("Administracion de sucursales de la empresa."),
                        new Tag().name("04 Departments").description("Administracion de departamentos internos."),
                        new Tag().name("05 Positions").description("Administracion de puestos de trabajo."),
                        new Tag().name("06 Employees").description("Gestion de empleados, perfiles y bajas logicas."),
                        new Tag().name("07 Variations").description("Conceptos salariales que suman o descuentan en liquidaciones."),
                        new Tag().name("08 Payrolls").description("Liquidaciones de sueldo y consultas por empleado."),
                        new Tag().name("09 Vacations").description("Solicitudes y registros de vacaciones."),
                        new Tag().name("10 Licenses").description("Licencias de empleados y su estado de aprobacion."),
                        new Tag().name("11 Certificates").description("Carga, consulta y descarga de certificados PDF."),
                        new Tag().name("12 Complaints").description("Denuncias internas y seguimiento de revision."),
                        new Tag().name("13 Suspensions").description("Suspensiones de empleados y cambio de estado asociado.")
                ))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Pegue solo el token JWT obtenido desde POST /api/auth/login. Swagger agrega el prefijo Bearer automaticamente.")));
    }
}
