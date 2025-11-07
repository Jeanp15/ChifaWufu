package com.chifawufu.sistema_ventas.dto;

// Usamos un 'record' de Java moderno, es como una clase DTO simple.
public record DetallePedidoDTO(
    Long idProducto,
    Integer cantidad
) {
}