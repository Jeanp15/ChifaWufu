package com.chifawufu.sistema_ventas.service;

import com.chifawufu.sistema_ventas.dto.CierreCajaDTO;
import com.chifawufu.sistema_ventas.dto.VentaRequestDTO;
import com.chifawufu.sistema_ventas.model.*;
import com.chifawufu.sistema_ventas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects; // 1. AÑADE ESTE IMPORT

@Service
public class VentaService {

    @Autowired private VentaRepository ventaRepository;
    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private FacturaElectronicaRepository facturaElectronicaRepository;

    @Transactional
    public Venta registrarVenta(@NonNull VentaRequestDTO request) {
        
        // 2. CORRECCIÓN DE NULL SAFETY
        // Verificamos que el ID del pedido no sea nulo antes de usarlo
        Long idPedido = Objects.requireNonNull(request.idPedido(), "El ID del pedido no puede ser nulo");
        
        // 1. Encontrar el Pedido que se va a pagar
        Pedido pedido = pedidoRepository.findById(idPedido) // Usamos la variable verificada
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        // ... (el resto del método no cambia) ...
        
        if (!"PENDIENTE".equals(pedido.getEstado())) {
            throw new RuntimeException("Este pedido ya fue procesado o está " + pedido.getEstado());
        }

        String nombreUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario cajero = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RuntimeException("Cajero no encontrado"));

        Venta venta = new Venta(
            pedido, 
            cajero, 
            pedido.getCliente(),
            request.tipoComprobante(),
            request.metodoDePago()
        );
        Venta ventaGuardada = ventaRepository.save(venta);
        
        pedido.setEstado("PAGADO");
        pedidoRepository.save(pedido);

        String serie = request.tipoComprobante().equals("FACTURA") ? "F001" : "B001";
        String numero = String.format("%06d", ventaGuardada.getIdVenta());
        
        FacturaElectronica factura = new FacturaElectronica(serie, numero, ventaGuardada);
        facturaElectronicaRepository.save(factura);

        return ventaGuardada;
    }
    
    // ... (El resto de tus métodos: getVentasDelDia, getVentasPorRango, realizarCierreCaja) ...
    // ... (Esos ya están bien) ...

    public List<Venta> getVentasDelDia(@NonNull LocalDate fecha) {
        LocalDateTime inicioDelDia = fecha.atStartOfDay();
        LocalDateTime finDelDia = fecha.atTime(LocalTime.MAX);
        return ventaRepository.findByFechaBetween(inicioDelDia, finDelDia);
    }

    public List<Venta> getVentasPorRango(@NonNull LocalDate fechaInicio, @NonNull LocalDate fechaFin) {
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);
        return ventaRepository.findByFechaBetween(inicio, fin);
    }

    public CierreCajaDTO realizarCierreCaja(@NonNull LocalDate fecha) {
        List<Venta> ventasDelDia = getVentasDelDia(fecha);
        BigDecimal totalGeneral = BigDecimal.ZERO;
        BigDecimal totalEfectivo = BigDecimal.ZERO;
        BigDecimal totalTarjeta = BigDecimal.ZERO;
        BigDecimal totalOtros = BigDecimal.ZERO;
        BigDecimal totalDelivery = BigDecimal.ZERO;
        BigDecimal totalSalon = BigDecimal.ZERO;

        for (Venta venta : ventasDelDia) {
            totalGeneral = totalGeneral.add(venta.getTotal());
            if ("EFECTIVO".equalsIgnoreCase(venta.getMetodoDePago())) {
                totalEfectivo = totalEfectivo.add(venta.getTotal());
            } else if ("TARJETA".equalsIgnoreCase(venta.getMetodoDePago())) {
                totalTarjeta = totalTarjeta.add(venta.getTotal());
            } else {
                totalOtros = totalOtros.add(venta.getTotal());
            }
            if ("DELIVERY".equalsIgnoreCase(venta.getPedido().getTipo())) {
                totalDelivery = totalDelivery.add(venta.getTotal());
            } else {
                totalSalon = totalSalon.add(venta.getTotal());
            }
        }
        return new CierreCajaDTO(
            ventasDelDia.size(), totalGeneral, totalEfectivo, totalTarjeta,
            totalOtros, totalDelivery, totalSalon, ventasDelDia
        );
    }
}