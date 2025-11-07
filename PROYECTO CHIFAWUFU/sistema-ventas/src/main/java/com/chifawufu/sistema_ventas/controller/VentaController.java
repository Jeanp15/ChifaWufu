package com.chifawufu.sistema_ventas.controller;

import com.chifawufu.sistema_ventas.dto.VentaRequestDTO;
import com.chifawufu.sistema_ventas.model.Venta;
import com.chifawufu.sistema_ventas.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "http://localhost:3000")
public class VentaController {
    
    @Autowired
    private VentaService ventaService;

    // Endpoint para registrar la venta (Cobrar)
    // Según tu PDF (CU04), esto lo hace el Cajero
    @PostMapping
    @PreAuthorize("hasAnyRole('Cajero', 'Administrador')")
    public ResponseEntity<?> registrarVenta(@RequestBody VentaRequestDTO ventaRequest) {
        try {
            Venta ventaCreada = ventaService.registrarVenta(ventaRequest);
            return ResponseEntity.ok(ventaCreada);
        } catch (RuntimeException e) {
            // Si el pedido no existe o ya fue pagado
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Aquí podrías agregar endpoints GET para consultar Ventas (RF09, RF10)
}