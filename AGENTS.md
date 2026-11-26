# AGENTS.md — gestion-prestamos-api

Guía local para agentes que trabajen en este repositorio.

## Proyecto

- API REST para gestión de clientes y préstamos.
- Java 17, Spring Boot 3.3.5, Maven (wrapper incluido).
- Paquete base: `com.gdev.gestionprestamosapi`.

## Stack y dependencias clave

- Spring Web, Validation, Data JPA, Security.
- H2 (runtime) y driver PostgreSQL.
- JWT (jjwt 0.12.6).
- Lombok.
- JUnit 5 + Mockito + Spring Security Test.

## Comandos

- Ejecutar aplicación: `./mvnw spring-boot:run`
- Ejecutar tests: `./mvnw test`
- Build completo: `./mvnw clean test`
- Docker: `docker build -t gestion-prestamos-api .` y `docker run -p 8080:8080 gestion-prestamos-api`

## Configuración crítica

- Configuración en `src/main/resources/application.yaml`.
- Variables de entorno relevantes: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JPA_DDL_AUTO`, `JPA_FORMAT_SQL`, `H2_CONSOLE_ENABLED`, `GDEV_LICENSE_VALIDATION_URL`, `JWT_SECRET` (mínimo 32 bytes), `JWT_EXPIRATION_MS`.
- La API expone endpoints bajo `/api/v1` (clientes/préstamos en memoria y auth) y `/api/loans` (préstamos JPA).
- No hardcodear secretos, IPs públicas ni credenciales.

## Arquitectura

- Controladores en `controller/`.
- Servicios en `service/`.
- Modelos en `model/`.
- DTOs en `dto/`.
- Hay dos flujos de préstamos: en memoria (`Prestamo`/`PrestamoController`/`PrestamoService`) y JPA (`Loan`/`LoanController`/`LoanService`).

## Reglas de trabajo (GDEV)

- Trabajar siempre en ramas `feature/` o `fix/` creadas desde `dev`; nunca directamente en `main`, `master` o `dev`.
- No hacer merge ni abrir PRs; solo commit y push de la rama.
- Planificar antes de modificar código y validar con `./mvnw clean test` antes de declarar terminado.
- Mantener cambios consistentes con el estilo y patrones existentes.

## Notas críticas

- `PrestamoController.crearPrestamo` recibe un `Map<String, Object>`; asegurar que `monto` y `clienteId` se mapeen a sus campos correspondientes.
- `ClienteService` y `PrestamoService` usan `ConcurrentHashMap` en memoria.
- El cálculo de intereses difiere entre flujo en memoria (5% VIP / 10% regular sobre monto) y flujo JPA (interés simple anual).
- Existe `openapi.yaml` como contrato de la API y `schema-oracle.sql` como referencia DDL.