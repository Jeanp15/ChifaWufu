package com.chifawufu.sistema_ventas.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cliente")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;
    
    private String nombre;
    private String dni;
    private String telefono;
    private String direccion;
    private String correo;
    
    @OneToMany(mappedBy = "cliente")
    private List<Venta> ventas;
    
    // Constructores, Getters y Setters...
    public Cliente() {}
    
    public Cliente(String nombre, String dni, String telefono) {
        this.nombre = nombre;
        this.dni = dni;
        this.telefono = telefono;
    }
    
    // Getters y Setters...
}