package com.chifawufu.sistema_ventas.model;

// --- 1. ¡QUITA ESTA IMPORTACIÓN! ---
// import com.fasterxml.jackson.annotation.JsonIgnore; 
// ---------------------------------

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

    @Column(length = 20)
    private String metodoDePago;
    
    // --- Relaciones ---
    
    @OneToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;
    
    // --- 2. ¡QUITA EL @JsonIgnore DE AQUÍ! ---
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;
    
    // --- Constructores ---
    
    public Venta() {}
    
    public Venta(Pedido pedido, Usuario usuario, Cliente cliente, String tipoComprobante, String metodoDePago) {
        this.pedido = pedido;
        this.usuario = usuario;
        this.cliente = cliente;
        this.tipoComprobante = tipoComprobante;
        this.total = pedido.getTotal(); 
        this.metodoDePago = metodoDePago;
    }
    
    // --- Getters y Setters ---
    // (El resto de tu clase sigue igual)
    // ...
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

    public String getMetodoDePago() {
        return metodoDePago;
    }

    public void setMetodoDePago(String metodoDePago) {
        this.metodoDePago = metodoDePago;
    }
}
