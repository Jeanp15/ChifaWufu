package com.chifawufu.sistema_ventas.model;

// --- 1. AÑADE ESTA IMPORTACIÓN ---
import com.fasterxml.jackson.annotation.JsonIgnore;
// ---------------------------------

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_pedido")
public class DetallePedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalle;
    
    @Column(nullable = false)
    private Integer cantidad;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    // --- Relaciones ---
    
    // DetallePedido <-> Pedido (Muchos a Uno)
    // Muchos detalles pertenecen a un pedido
    
    // --- 2. AÑADE ESTA ANOTACIÓN AQUÍ ---
    @JsonIgnore // Esto rompe el bucle infinito (Pedido -> DetallePedido -> Pedido)
    // -----------------------------------
    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;
    
    // DetallePedido <-> Producto (Muchos a Uno)
    // Muchos detalles (líneas de pedido) pueden apuntar a un producto
    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;
    
    // --- Constructores ---
    
    public DetallePedido() {}
    
    public DetallePedido(Pedido pedido, Producto producto, Integer cantidad) {
        this.pedido = pedido;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecio();
        this.subtotal = this.precioUnitario.multiply(new BigDecimal(cantidad));
    }
    
    // --- Getters y Setters ---

    public Long getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Long idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
}