package com.chifawufu.sistema_ventas.service;


import com.chifawufu.sistema_ventas.model.Producto;
import com.chifawufu.sistema_ventas.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }
    
    public List<Producto> findActivos() {
        return productoRepository.findByActivoTrue();
    }

    // 2. AÑADE @NonNull
    public Optional<Producto> findById(@NonNull Long id) {
        return productoRepository.findById(id);
    }

    // 3. AÑADE @NonNull
    public Producto save(@NonNull Producto producto) {
        return productoRepository.save(producto);
    }

    // 4. AÑADE @NonNull
    public void deleteById(@NonNull Long id) {
        productoRepository.deleteById(id);
    }
}