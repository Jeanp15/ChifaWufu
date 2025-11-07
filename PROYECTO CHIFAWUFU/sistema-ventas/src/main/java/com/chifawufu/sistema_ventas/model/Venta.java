package com.chifawufu.sistema_ventas.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "venta")
public class Venta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVenta;
    
    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @Column(nullable = false, length = 20)
    private String tipoComprobante;

    // 1. CAMPO NUEVO
    @Column(length = 20)
    private String metodoDePago; // "EFECTIVO", "TARJETA", "YAPE", etc.
    
    // --- Relaciones ---
    
    // Venta <-> Pedido (Uno a Uno)
    // Una venta se genera a partir de un pedido
    @OneToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;
    
    // Venta <-> Usuario (Muchos a Uno)
    // Muchas ventas pueden ser registradas por un usuario
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    
    // Venta <-> Cliente (Muchos a Uno)
    // Muchas ventas pueden pertenecer a un cliente
    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;
    
    // --- Constructores ---
    
    public Venta() {}
    
    // 2. CONSTRUCTOR MODIFICADO (se añadió metodoDePago)
    public Venta(Pedido pedido, Usuario usuario, Cliente cliente, String tipoComprobante, String metodoDePago) {
        this.pedido = pedido;
        this.usuario = usuario;
        this.cliente = cliente;
        this.tipoComprobante = tipoComprobante;
        this.total = pedido.getTotal(); // El total de la venta es el total del pedido
        this.metodoDePago = metodoDePago; // 3. ASIGNACIÓN DEL NUEVO CAMPO
    }
    
    // --- Getters y Setters ---

    public Long getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Long idVenta) {
        this.idVenta = idVenta;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    // 4. GETTERS Y SETTERS NUEVOS
    public String getMetodoDePago() {
        return metodoDePago;
    }

    public void setMetodoDePago(String metodoDePago) {
        this.metodoDePago = metodoDePago;
    }
}