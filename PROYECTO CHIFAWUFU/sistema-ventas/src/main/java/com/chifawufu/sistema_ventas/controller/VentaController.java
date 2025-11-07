package com.chifawufu.sistema_ventas.controller;

import com.chifawufu.sistema_ventas.dto.CierreCajaDTO; // 1. IMPORT NUEVO
import com.chifawufu.sistema_ventas.dto.VentaRequestDTO;
import com.chifawufu.sistema_ventas.model.Venta;
import com.chifawufu.sistema_ventas.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// Imports para los reportes
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

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
    
    // --- ENDPOINTS DE REPORTES (RF09 y RF10) ---
    // Protegidos solo para el Administrador

    /**
     * Reporte de Ventas del Día (RF09)
     * El frontend llamará a: GET /api/ventas/reporte/dia?fecha=2025-11-07
     */
    @GetMapping("/reporte/dia")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<List<Venta>> getReporteVentasDelDia(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        return ResponseEntity.ok(ventaService.getVentasDelDia(fecha));
    }

    /**
     * Reporte de Ventas por Rango de Fechas (RF10)
     * El frontend llamará a: GET /api/ventas/reporte/rango?inicio=2025-11-01&fin=2025-11-07
     */
    @GetMapping("/reporte/rango")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<List<Venta>> getReporteVentasPorRango(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        
        return ResponseEntity.ok(ventaService.getVentasPorRango(inicio, fin));
    }

    // 2. MÉTODO NUEVO PARA CIERRE DE CAJA (CU10)
    /**
     * Endpoint para el Cierre de Caja (CU10)
     * El frontend llamará a: GET /api/ventas/cierre-caja?fecha=2025-11-07
     */
    @GetMapping("/cierre-caja")
    @PreAuthorize("hasAnyRole('Cajero', 'Administrador')") // Protegido para Cajero y Admin
    public ResponseEntity<CierreCajaDTO> getCierreCaja(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        
        CierreCajaDTO cierre = ventaService.realizarCierreCaja(fecha);
        return ResponseEntity.ok(cierre);
    }
}