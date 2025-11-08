package com.chifawufu.sistema_ventas.controller;

import com.chifawufu.sistema_ventas.dto.CierreCajaDTO;
import com.chifawufu.sistema_ventas.dto.VentaRequestDTO;
import com.chifawufu.sistema_ventas.model.Venta;
import com.chifawufu.sistema_ventas.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

@RestController
@RequestMapping("/api/ventas")
// LA LÍNEA @CrossOrigin SE HA ELIMINADO
public class VentaController {
    
    @Autowired
    private VentaService ventaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('Cajero', 'Administrador')")
    public ResponseEntity<?> registrarVenta(@RequestBody @NonNull VentaRequestDTO ventaRequest) {
        try {
            Venta ventaCreada = ventaService.registrarVenta(ventaRequest);
            return ResponseEntity.ok(ventaCreada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/reporte/dia")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<List<Venta>> getReporteVentasDelDia(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NonNull LocalDate fecha) {
        
        return ResponseEntity.ok(ventaService.getVentasDelDia(fecha));
    }

    @GetMapping("/reporte/rango")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<List<Venta>> getReporteVentasPorRango(
            @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NonNull LocalDate inicio,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NonNull LocalDate fin) {
        
        return ResponseEntity.ok(ventaService.getVentasPorRango(inicio, fin));
    }

    @GetMapping("/cierre-caja")
    @PreAuthorize("hasAnyRole('Cajero', 'Administrador')")
    public ResponseEntity<CierreCajaDTO> getCierreCaja(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @NonNull LocalDate fecha) {
        
        CierreCajaDTO cierre = ventaService.realizarCierreCaja(fecha);
        return ResponseEntity.ok(cierre);
    }
}