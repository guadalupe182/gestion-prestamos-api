package com.gdev.gestionprestamosapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Prestamo {
    private String id;
    private double monto;
    private String clienteId;
    private LocalDate fecha;
    private EstadoPrestamo estado;
}
