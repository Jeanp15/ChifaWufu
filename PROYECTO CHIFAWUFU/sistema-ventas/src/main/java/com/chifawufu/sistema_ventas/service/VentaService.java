package com.chifawufu.sistema_ventas.service;

import com.chifawufu.sistema_ventas.dto.CierreCajaDTO; // IMPORT NUEVO
import com.chifawufu.sistema_ventas.dto.VentaRequestDTO;
import com.chifawufu.sistema_ventas.model.*;
import com.chifawufu.sistema_ventas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal; // IMPORT NUEVO
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

        // 4. Crear la Venta (MODIFICADO para incluir metodoDePago)
        Venta venta = new Venta(
            pedido, 
            cajero, 
            pedido.getCliente(), // El cliente se asignó al crear el pedido
            request.tipoComprobante(),
            request.metodoDePago() // DATO NUEVO
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
    
    // --- MÉTODOS DE REPORTE (Ya existentes) ---

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

    // --- MÉTODO NUEVO PARA CIERRE DE CAJA (CU10) ---

    /**
     * Realiza el Cierre de Caja para una fecha dada (CU10)
     * @param fecha La fecha para el cierre
     * @return Un DTO con todos los totales calculados
     */
    public CierreCajaDTO realizarCierreCaja(LocalDate fecha) {
        
        // 1. Obtenemos la lista de ventas (¡reutilizamos el método!)
        List<Venta> ventasDelDia = getVentasDelDia(fecha);
        
        // 2. Inicializamos los contadores
        BigDecimal totalGeneral = BigDecimal.ZERO;
        BigDecimal totalEfectivo = BigDecimal.ZERO;
        BigDecimal totalTarjeta = BigDecimal.ZERO;
        BigDecimal totalOtros = BigDecimal.ZERO;
        BigDecimal totalDelivery = BigDecimal.ZERO;
        BigDecimal totalSalon = BigDecimal.ZERO;

        // 3. Recorremos las ventas y sumamos
        for (Venta venta : ventasDelDia) {
            totalGeneral = totalGeneral.add(venta.getTotal());
            
            // Sumar por método de pago (CU10)
            if ("EFECTIVO".equalsIgnoreCase(venta.getMetodoDePago())) {
                totalEfectivo = totalEfectivo.add(venta.getTotal());
            } else if ("TARJETA".equalsIgnoreCase(venta.getMetodoDePago())) {
                totalTarjeta = totalTarjeta.add(venta.getTotal());
            } else {
                totalOtros = totalOtros.add(venta.getTotal());
            }
            
            // Sumar por tipo de pedido (CU10)
            if ("DELIVERY".equalsIgnoreCase(venta.getPedido().getTipo())) {
                totalDelivery = totalDelivery.add(venta.getTotal());
            } else { // Asumimos que todo lo que no es delivery es "SALON"
                totalSalon = totalSalon.add(venta.getTotal());
            }
        }

        // 4. Creamos el objeto de respuesta
        return new CierreCajaDTO(
            ventasDelDia.size(),
            totalGeneral,
            totalEfectivo,
            totalTarjeta,
            totalOtros,
            totalDelivery,
            totalSalon,
            ventasDelDia
        );
    }
}