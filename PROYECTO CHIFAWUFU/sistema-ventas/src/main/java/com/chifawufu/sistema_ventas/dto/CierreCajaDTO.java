package com.chifawufu.sistema_ventas.dto;

import java.math.BigDecimal;
import java.util.List;
import com.chifawufu.sistema_ventas.model.Venta;

// Usamos un 'record' para este DTO simple
public record CierreCajaDTO(
    long totalTransacciones,
    BigDecimal totalGeneral,
    BigDecimal totalEfectivo,
    BigDecimal totalTarjeta,
    BigDecimal totalOtrosMetodos, // Para Yape, Plin, etc.
    BigDecimal totalDelivery, // Total de pedidos tipo DELIVERY
    BigDecimal totalSalon,     // Total de pedidos tipo SALON
    List<Venta> ventasDelDia // La lista detallada
) {
}