package com.chifawufu.sistema_ventas.controller;

import com.chifawufu.sistema_ventas.dto.PedidoRequestDTO;
import com.chifawufu.sistema_ventas.model.Pedido;
import com.chifawufu.sistema_ventas.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:3000")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // Endpoint para crear un nuevo pedido
    // Protegido por roles según tu PDF
    @PostMapping
    @PreAuthorize("hasAnyRole('Cajero', 'Mozo', 'Administrador')")
    public ResponseEntity<Pedido> crearPedido(@RequestBody PedidoRequestDTO pedidoRequest) {
        try {
            Pedido pedidoCreado = pedidoService.crearPedido(pedidoRequest);
            return ResponseEntity.ok(pedidoCreado);
        } catch (RuntimeException e) {
            // Si algo falla (ej. sin stock), enviamos un mal request
            return ResponseEntity.badRequest().body(null); 
            // En un proyecto real, aquí devolverías un mensaje de error JSON
        }
    }
    
    /**
     * Endpoint para la Cocina.
     * Obtiene todos los pedidos que están PENDIENTES.
     * (CU09)
     */
    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('Cocinero', 'Administrador')")
    public ResponseEntity<List<Pedido>> getPedidosPendientes() {
        List<Pedido> pedidos = pedidoService.findByEstado("PENDIENTE");
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Endpoint para cambiar el estado de un pedido
     * (Ej. La cocina lo marca como "EN_PREPARACION" o "LISTO")
     */
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('Cocinero', 'Administrador')")
    public ResponseEntity<Pedido> actualizarEstadoPedido(
            @PathVariable Long id, 
            @RequestParam String estado) {
        
        try {
            Pedido pedido = pedidoService.actualizarEstado(id, estado);
            return ResponseEntity.ok(pedido);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}