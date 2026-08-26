package com.gdev.gestionprestamosapi.service;

import com.gdev.gestionprestamosapi.model.EstadoPrestamo;
import com.gdev.gestionprestamosapi.model.Prestamo;
import com.gdev.gestionprestamosapi.model.TipoCliente;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class PrestamoService {
    //DB en memopria SOLO para PRESTAMOS
    private final Map<String, Prestamo> prestamosDb = new ConcurrentHashMap<>();

    //Inyeccion de dependencia: traemos el servicio de clientes
    private final ClienteService clienteService;

    public PrestamoService(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    public Prestamo crearPrestamo(double monto, String clienteId){
        log.info("Creando prestamo para cliente ID:{}", clienteId );

        // Delegamos la busqueda del cliente a su propio service
        var cliente  = clienteService.obtenerClienteporId(clienteId);
        if(cliente == null){
            log.error("Cliente ID {} no encontrado",  clienteId);
            throw new IllegalArgumentException("El cliente asignado no existe");
        }
        var id = UUID.randomUUID().toString();
        var prestamo =  new Prestamo(id, monto, clienteId, LocalDate.now(), EstadoPrestamo.PENDIENTE);
        prestamosDb.put(id, prestamo);
        return prestamo;
    }

    public java.util.List<Prestamo> obtenerPrestamosActivos(){
        return prestamosDb.values().stream()
                .filter(p -> p.getEstado() == EstadoPrestamo.PENDIENTE)
                .toList();
    }

    public void actualizarPrestamo(String id, EstadoPrestamo nuevoEstado){
        var prestamo = prestamosDb.get(id);
        if(prestamo == null){
            throw new java.util.NoSuchElementException("El prestamo no existe");
        }
        prestamo.setEstado(nuevoEstado);
        log.info("Estado del prestamo {} actualizado a {}", id, nuevoEstado);
    }
    public void eliminarPrestamo(String id){
        prestamosDb.remove(id);
        log.info("Prestamo {} eliminado", id);
    }

    //---CALCULO DE INTERESES CON PATTERN MATCHING---
    public Double calcularMontoTotalAPagar(String prestamoId){
        var prestamo = prestamosDb.get(prestamoId);
        if(prestamo == null){
            throw new java.util.NoSuchElementException("Prestamo no encontrado");
        }

        //Consultamos al servicio de clientes para obtener el objeto
        Object objCliente = clienteService.obtenerClienteporId(prestamo.getClienteId());

        //Pattern en accion
        var tasaInteres = 0.10; //Default regular 10%

        if(objCliente instanceof  com.gdev.gestionprestamosapi.model.Cliente c &&
        c.tipoCliente() == TipoCliente.VIP){

            tasaInteres = 0.05; // Beneficio VIP 5%
            log.info("Descuento VIP aplicado (5%) para el cliente: {}", c.nombre());
        }

        var total = prestamo.getMonto() * (1 + tasaInteres);
        log.info("Monto original: ${}, Total a pagar: ${}", prestamo.getMonto(), total);
        return total;
    }
}
