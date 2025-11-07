package com.chifawufu.sistema_ventas.dto;

import java.util.List;

public record PedidoRequestDTO(
    String tipo, // "SALON" o "DELIVERY"
    String mesa, // Opcional, solo para SALON
    Long idCliente, // Opcional, para DELIVERY o cliente registrado
    List<DetallePedidoDTO> detalles // La lista de productos y cantidades
) {
}