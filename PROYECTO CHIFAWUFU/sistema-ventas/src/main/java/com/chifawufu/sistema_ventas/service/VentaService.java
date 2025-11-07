package com.chifawufu.sistema_ventas.service;

import com.chifawufu.sistema_ventas.dto.VentaRequestDTO;
import com.chifawufu.sistema_ventas.model.*;
import com.chifawufu.sistema_ventas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Imports para los reportes
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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
    
    // --- MÉTODOS DE REPORTE (LOS NUEVOS VAN AQUÍ) ---

    /**
     * Obtiene todas las ventas de una fecha específica. (RF09)
     * @param fecha La fecha para el reporte (ej. 2025-11-07)
     * @return Lista de ventas de ese día
     */
    public List<Venta> getVentasDelDia(LocalDate fecha) {
        // Define el inicio del día (00:00:00)
        LocalDateTime inicioDelDia = fecha.atStartOfDay();
        // Define el fin del día (23:59:59.999...)
        LocalDateTime finDelDia = fecha.atTime(LocalTime.MAX);
        
        return ventaRepository.findByFechaBetween(inicioDelDia, finDelDia);
    }

    /**
     * Obtiene todas las ventas en un rango de fechas. (RF10)
     * @param fechaInicio La fecha inicial del rango
     * @param fechaFin La fecha final del rango
     * @return Lista de ventas en ese rango
     */
    public List<Venta> getVentasPorRango(LocalDate fechaInicio, LocalDate fechaFin) {
        // Define el inicio del rango (ej. 2025-11-01 00:00:00)
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        // Define el fin del rango (ej. 2025-11-07 23:59:59.999...)
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);
        
        return ventaRepository.findByFechaBetween(inicio, fin);
    }
}