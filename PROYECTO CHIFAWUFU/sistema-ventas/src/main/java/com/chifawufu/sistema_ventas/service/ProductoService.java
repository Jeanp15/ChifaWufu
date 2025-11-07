package com.chifawufu.sistema_ventas.service;

import com.chifawufu.sistema_ventas.model.Producto;
import com.chifawufu.sistema_ventas.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // Obtener todos los productos
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }
    
    // Obtener solo los productos activos (para el menú)
    public List<Producto> findActivos() {
        return productoRepository.findByActivoTrue();
    }

    // Obtener un producto por ID
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    // Guardar un producto (ya sea nuevo o para actualizar)
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    // Borrar un producto por ID
    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }
}