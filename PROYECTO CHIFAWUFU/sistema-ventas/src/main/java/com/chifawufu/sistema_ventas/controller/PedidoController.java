package com.chifawufu.sistema_ventas.controller;

import com.chifawufu.sistema_ventas.dto.PedidoRequestDTO;
import com.chifawufu.sistema_ventas.model.Pedido;
import com.chifawufu.sistema_ventas.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.lang.NonNull; // 1. AÑADE ESTE IMPORT
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:3000")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('Cajero', 'Mozo', 'Administrador')")
    // 2. AÑADE @NonNull
    public ResponseEntity<Pedido> crearPedido(@RequestBody @NonNull PedidoRequestDTO pedidoRequest) {
        try {
            Pedido pedidoCreado = pedidoService.crearPedido(pedidoRequest);
            return ResponseEntity.ok(pedidoCreado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null); 
        }
    }
    
    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('Cocinero', 'Administrador')")
    public ResponseEntity<List<Pedido>> getPedidosPendientes() {
        List<Pedido> pedidos = pedidoService.findByEstado("PENDIENTE");
        return ResponseEntity.ok(pedidos);
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('Cocinero', 'Administrador')")
    // 3. AÑADE @NonNull A AMBOS
    public ResponseEntity<Pedido> actualizarEstadoPedido(
            @PathVariable @NonNull Long id, 
            @RequestParam @NonNull String estado) {
        
        try {
            Pedido pedido = pedidoService.actualizarEstado(id, estado);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}