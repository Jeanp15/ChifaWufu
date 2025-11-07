package com.chifawufu.sistema_ventas.dto;

public record VentaRequestDTO(
    Long idPedido,
    String tipoComprobante // "BOLETA" o "FACTURA"
) {
}