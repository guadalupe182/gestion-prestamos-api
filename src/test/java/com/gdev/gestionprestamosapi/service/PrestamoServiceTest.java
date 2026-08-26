package com.gdev.gestionprestamosapi.service;

import com.gdev.gestionprestamosapi.model.Cliente;
import com.gdev.gestionprestamosapi.model.Prestamo;
import com.gdev.gestionprestamosapi.model.TipoCliente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrestamoServiceTest {

    @Mock
    private ClienteService clienteService; // Simulamos la base de datos de clientes

    @InjectMocks
    private PrestamoService prestamoService; // Inyectamos el mock dentro de nuestro servicio real

    @Test
    void calcularMontoTotalAPagar_ClienteVIP_Aplica5Porciento() {
        // 1. Arrange (Preparar)
        String clienteId = "vip-123";
        Cliente clienteVIP = new Cliente(clienteId, "Juan VIP", "juan@test.com", 30, TipoCliente.VIP);

        // Le decimos a Mockito: "Cuando te pidan este ID, devuelve el cliente VIP"
        when(clienteService.obtenerClienteporId(clienteId)).thenReturn(clienteVIP);

        // Creamos un préstamo de $1000
        Prestamo prestamo = prestamoService.crearPrestamo(1000.0, clienteId);

        // 2. Act (Ejecutar)
        Double totalAPagar = prestamoService.calcularMontoTotalAPagar(prestamo.getId());

        // 3. Assert (Comprobar: 1000 + 5% = 1050.0)
        assertEquals(1050.0, totalAPagar);
    }

    @Test
    void calcularMontoTotalAPagar_ClienteRegular_Aplica10Porciento() {
        // 1. Arrange
        String clienteId = "reg-456";
        Cliente clienteRegular = new Cliente(clienteId, "Pedro Regular", "pedro@test.com", 25, TipoCliente.REGULAR);

        when(clienteService.obtenerClienteporId(clienteId)).thenReturn(clienteRegular);

        Prestamo prestamo = prestamoService.crearPrestamo(1000.0, clienteId);

        // 2. Act
        Double totalAPagar = prestamoService.calcularMontoTotalAPagar(prestamo.getId());

        // 3. Assert (Comprobar: 1000 + 10% = 1100.0)
        assertEquals(1100.0, totalAPagar);
    }

    @Test
    void calcularMontoTotalAPagar_PrestamoNoExiste_LanzaExcepcion() {
        // Ejecutar y comprobar que lanza NoSuchElementException si el ID es inventado
        assertThrows(NoSuchElementException.class, () -> {
            prestamoService.calcularMontoTotalAPagar("id-fantasma");
        });
    }
}