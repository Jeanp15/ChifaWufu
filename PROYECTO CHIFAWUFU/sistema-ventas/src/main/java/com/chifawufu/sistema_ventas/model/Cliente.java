package com.chifawufu.sistema_ventas.model;

import jakarta.persistence.*;
import java.util.List;
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
    
    // --- Getters y Setters ---

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public java.util.List<Venta> getVentas() {
        return ventas;
    }

    public void setVentas(java.util.List<Venta> ventas) {
        this.ventas = ventas;
    }
}