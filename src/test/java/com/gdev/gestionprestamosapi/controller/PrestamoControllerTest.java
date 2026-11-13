package com.gdev.gestionprestamosapi.controller;

import com.gdev.gestionprestamosapi.model.EstadoPrestamo;
import com.gdev.gestionprestamosapi.model.Prestamo;
import com.gdev.gestionprestamosapi.service.PrestamoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrestamoControllerTest {

    @Mock
    private PrestamoService prestamoService;

    @InjectMocks
    private PrestamoController prestamoController;

    @Test
    void crearPrestamo_MapeaMontoYClienteIdCorrectamente() {
        Map<String, Object> body = Map.of("monto", 1500.0, "clienteId", "cli-1");
        Prestamo prestamoEsperado = new Prestamo("id-1", 1500.0, "cli-1", LocalDate.now(), EstadoPrestamo.PENDIENTE);

        when(prestamoService.crearPrestamo(1500.0, "cli-1")).thenReturn(prestamoEsperado);

        ResponseEntity<Prestamo> response = prestamoController.crearPrestamo(body);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1500.0, response.getBody().getMonto());
        assertEquals("cli-1", response.getBody().getClienteId());
        verify(prestamoService).crearPrestamo(1500.0, "cli-1");
    }
}