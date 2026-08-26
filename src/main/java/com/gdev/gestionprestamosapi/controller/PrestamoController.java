package com.gdev.gestionprestamosapi.controller;

import com.gdev.gestionprestamosapi.model.Prestamo;
import com.gdev.gestionprestamosapi.service.PrestamoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @PostMapping
    public ResponseEntity<Prestamo> crearPrestamo(@RequestBody Map<String, Object> body){
        double monto = Double.parseDouble(body.get("clienteId").toString());
        String clienteId = body.get("clienteId").toString();
        return new ResponseEntity<>(prestamoService.crearPrestamo(monto, clienteId), HttpStatus.CREATED);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Prestamo>> listarActivos(){
        return ResponseEntity.ok(prestamoService.obtenerPrestamosActivos());
    }

    @PatchMapping("/{id}/total-pagar")
    public ResponseEntity<Map<String, Object>> calcularTotalPagar(@PathVariable String id){
        double total = prestamoService.calcularMontoTotalAPagar(id);
        //Retorna JSON con el total
        return ResponseEntity.ok(Map.of("pretamoId",id, "total", total));
    }
}
