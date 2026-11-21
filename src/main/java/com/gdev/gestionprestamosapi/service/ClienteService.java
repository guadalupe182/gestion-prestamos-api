package com.gdev.gestionprestamosapi.service;

import com.gdev.gestionprestamosapi.model.Cliente;
import com.gdev.gestionprestamosapi.model.Prestamo;
import com.gdev.gestionprestamosapi.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ClienteService {

    //db en memoria solo para clientes
    private final Map<String, Cliente> clientesDb = new ConcurrentHashMap<>();

    public Cliente crearCliente(Cliente cliente) {
        log.info("Registrando cliente con email {}", cliente.email());

        // Genera el ID con var
        var id = UUID.randomUUID().toString();
        String tenantId = SecurityUtils.getCurrentUsername();

        // Crear el nuevo cliente con var extrayendo los datos del que llegó
        var nuevoCliente = new Cliente(id, cliente.nombre(), cliente.email(), cliente.edad(), cliente.tipoCliente(), tenantId);

        // Guardar cliente en memoria
        clientesDb.put(id, nuevoCliente);

        return nuevoCliente;
    }

    public List<Cliente> obtenerTodosLosClientes() {
        String tenantId = SecurityUtils.getCurrentUsername();
        return clientesDb.values().stream()
                .filter(c -> tenantId.equals(c.tenantId()))
                .toList();
    }

    public Cliente obtenerClienteporId(String id) {
        Cliente cliente = clientesDb.get(id);
        if (cliente != null && !SecurityUtils.getCurrentUsername().equals(cliente.tenantId())) {
            return null;
        }
        return cliente;
    }

    public void actualizarCliente(String id, Cliente cliente) {
        Cliente existente = clientesDb.get(id);
        if (existente == null) {
            throw new NoSuchElementException("El cliente a actualizar no existe");
        }
        if (!SecurityUtils.getCurrentUsername().equals(existente.tenantId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El cliente no pertenece al usuario autenticado");
        }
        var actializado = new Cliente(id, cliente.nombre(), cliente.email(), cliente.edad(), cliente.tipoCliente(), existente.tenantId());
        clientesDb.put(id, actializado);
        log.info("Cliente actualizado correctamente");
    }

    public void eliminarCliente(String id) {
        Cliente existente = clientesDb.get(id);
        if (existente != null && !SecurityUtils.getCurrentUsername().equals(existente.tenantId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El cliente no pertenece al usuario autenticado");
        }
        clientesDb.remove(id);
        log.info("Cliente eliminado correctamente");
    }
}
