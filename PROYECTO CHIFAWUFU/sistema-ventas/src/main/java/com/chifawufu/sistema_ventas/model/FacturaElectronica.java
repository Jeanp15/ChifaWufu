package com.chifawufu.sistema_ventas.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "factura_electronica")
public class FacturaElectronica {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFactura;
    
    @Column(length = 10)
    private String serie;
    
    @Column(length = 10)
    private String numero;
    
    @Column(nullable = false)
    private LocalDate fechaEmision = LocalDate.now();
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    // Relación: Una Factura le pertenece a una Venta
    @OneToOne
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;
    
    // --- Constructores ---
    public FacturaElectronica() {}
    
    public FacturaElectronica(String serie, String numero, Venta venta) {
        this.serie = serie;
        this.numero = numero;
        this.venta = venta;
        this.total = venta.getTotal();
    }
    
    // --- Getters y Setters ---
    // (Puedes agregarlos aquí si los necesitas, 
    // o pedirlos a tu IDE)
}