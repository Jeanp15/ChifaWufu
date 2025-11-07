package com.chifawufu.sistema_ventas.service;

import com.chifawufu.sistema_ventas.dto.VentaRequestDTO;
import com.chifawufu.sistema_ventas.model.*;
import com.chifawufu.sistema_ventas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VentaService {

    @Autowired private VentaRepository ventaRepository;
    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private FacturaElectronicaRepository facturaElectronicaRepository;
    // (Podrías necesitar un servicio para generar correlativos (ej. B001-001),
    // pero por ahora lo haremos simple)

    @Transactional
    public Venta registrarVenta(VentaRequestDTO request) {
        
        // 1. Encontrar el Pedido que se va a pagar
        Pedido pedido = pedidoRepository.findById(request.idPedido())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        // 2. Validar que el pedido no haya sido pagado
        if (!"PENDIENTE".equals(pedido.getEstado())) {
            throw new RuntimeException("Este pedido ya fue procesado o está " + pedido.getEstado());
        }

        // 3. Obtener al Cajero (Usuario) que está logueado
        String nombreUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario cajero = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RuntimeException("Cajero no encontrado"));

        // 4. Crear la Venta
        Venta venta = new Venta(
            pedido, 
            cajero, 
            pedido.getCliente(), // El cliente se asignó al crear el pedido
            request.tipoComprobante()
        );
        Venta ventaGuardada = ventaRepository.save(venta);
        
        // 5. Actualizar el Pedido a "PAGADO"
        pedido.setEstado("PAGADO");
        pedidoRepository.save(pedido);

        // 6. Simular la generación de Factura/Boleta (RF04)
        // En un proyecto real, aquí llamarías al API de la SUNAT/PSE
        // Usaremos un número simple por ahora
        String serie = request.tipoComprobante().equals("FACTURA") ? "F001" : "B001";
        String numero = String.format("%06d", ventaGuardada.getIdVenta()); // Ej: "000001"
        
        FacturaElectronica factura = new FacturaElectronica(serie, numero, ventaGuardada);
        facturaElectronicaRepository.save(factura);

        return ventaGuardada;
    }
}