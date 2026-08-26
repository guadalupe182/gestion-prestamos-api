package com.gdev.gestionprestamosapi.model;

public record Cliente(
   String id,
   String nombre,
   String email,
   int edad,
   TipoCliente tipoCliente
) {}
