package com.gdev.gestionprestamosapi.model;

public record Cliente(
   String id,
   String nombre,
   String email,
   int edad,
   TipoCliente tipoCliente,
   String tenantId
) {
    public Cliente(String id, String nombre, String email, int edad, TipoCliente tipoCliente) {
        this(id, nombre, email, edad, tipoCliente, null);
    }
}