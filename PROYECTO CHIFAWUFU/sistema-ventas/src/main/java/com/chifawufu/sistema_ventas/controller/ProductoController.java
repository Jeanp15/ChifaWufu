package com.chifawufu.sistema_ventas.controller;

import com.chifawufu.sistema_ventas.model.Producto;
import com.chifawufu.sistema_ventas.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // --- Endpoints de LECTURA (Read) ---

    // Obtener TODOS los productos (para el admin)
    @GetMapping
    @PreAuthorize("hasRole('Administrador')")
    public List<Producto> getAllProductos() {
        return productoService.findAll();
    }
    
    // Obtener solo productos ACTIVOS (para el menú del Mozo/Cajero)
    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('Administrador', 'Cajero', 'Mozo')")
    public List<Producto> getProductosActivos() {
        return productoService.findActivos();
    }

    // Obtener un producto por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Administrador', 'Cajero', 'Mozo')")
    public ResponseEntity<Producto> getProductoById(@PathVariable Long id) {
        Optional<Producto> producto = productoService.findById(id);
        return producto.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    // --- Endpoints de ESCRITURA (CUD) ---
    // (Solo el Administrador puede gestionar el catálogo según CU02)

    // Crear un nuevo producto
    @PostMapping
    @PreAuthorize("hasRole('Administrador')")
    public Producto createProducto(@RequestBody Producto producto) {
        return productoService.save(producto);
    }

    // Actualizar un producto existente
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<Producto> updateProducto(@PathVariable Long id, @RequestBody Producto productoDetails) {
        Optional<Producto> productoOptional = productoService.findById(id);

        if (productoOptional.isPresent()) {
            Producto productoExistente = productoOptional.get();
            productoExistente.setNombre(productoDetails.getNombre());
            productoExistente.setDescripcion(productoDetails.getDescripcion());
            productoExistente.setPrecio(productoDetails.getPrecio());
            productoExistente.setCategoria(productoDetails.getCategoria());
            productoExistente.setStock(productoDetails.getStock());
            productoExistente.setActivo(productoDetails.getActivo());
            
            return ResponseEntity.ok(productoService.save(productoExistente));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Borrar un producto
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        if (productoService.findById(id).isPresent()) {
            productoService.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}