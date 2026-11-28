# Diseño de roles (pendiente de implementación)

Este documento describe la estructura necesaria para agregar los roles `MANAGER` y `CASHIER` sin implementarlos todavía.

## Objetivo
Permitir autorización basada en roles además del aislamiento por tenant/usuario.

## Roles propuestos
- `USER`: acceso a sus propios recursos (comportamiento actual).
- `MANAGER`: acceso a todos los recursos de su tenant.
- `CASHIER`: acceso a operaciones de pagos y consulta de préstamos de su tenant.

## Cambios en modelo
- Agregar enum `Role { USER, MANAGER, CASHIER }`.
- `AppUser`:
  - campo `role` (enum o string) con default `USER`.
  - mantener `licenseKey`.
- Opcional: tabla `user_roles` si se requieren múltiples roles por usuario.

## Cambios en seguridad
- `JwtService`: incluir claim `role` (y opcionalmente `tenantId`) en el token.
- `SecurityFilter`: leer `role` del JWT y asignar `SimpleGrantedAuthority("ROLE_" + role)`.
- `SecurityConfig`: reglas por ruta/método según rol:
  - `/api/loans/**` GET: `USER`, `CASHIER`, `MANAGER`.
  - `/api/loans/**` POST/PUT/DELETE: `MANAGER` (o `USER` para propios recursos).
  - `/api/loans/{id}/payments`: `CASHIER`, `MANAGER`.
  - `/api/v1/clientes/**`: `MANAGER`.
- Servicios: validar rol en operaciones sensibles además del tenant.

## Migración
- Agregar columna `role` a `app_users` con default `USER`.
- Tokens emitidos antes del cambio no tendrán rol; se recomienda re-login.

## Pruebas sugeridas
- Unitarias de autorización por rol.
- Integración: usuario `CASHIER` no puede crear préstamos; `MANAGER` puede.