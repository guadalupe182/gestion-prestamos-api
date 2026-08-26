package com.gdev.gestionprestamosapi.service;

import com.gdev.gestionprestamosapi.model.Cliente;
import com.gdev.gestionprestamosapi.model.Prestamo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        // Crear el nuevo cliente con var extrayendo los datos del que llegó
        var nuevoCliente = new Cliente(id, cliente.nombre(), cliente.email(), cliente.edad(), cliente.tipoCliente());

        // Guardar cliente en memoria
        clientesDb.put(id, nuevoCliente);

        return nuevoCliente;
    }

    public List<Cliente> obtenerTodosLosClientes() {
        return  new ArrayList<>(clientesDb.values());
    }

    public Cliente obtenerClienteporId(String id) {
        return clientesDb.get(id);
    }

    public void actualizarCliente(String id, Cliente cliente) {
        if(!clientesDb.containsKey(id)) {
            throw new NoSuchElementException("El cliente a actualizar no existe");
        }
        var actializado = new Cliente(id, cliente.nombre(), cliente.email(), cliente.edad(), cliente.tipoCliente());
        clientesDb.put(id, cliente);
        log.info("Cliente actualizado correctamente");
    }

    public void eliminarCliente(String id) {
        clientesDb.remove(id);
        log.info("Cliente eliminado correctamente");
    }
}
