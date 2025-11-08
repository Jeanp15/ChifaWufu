package com.chifawufu.sistema_ventas.controller;

import com.chifawufu.sistema_ventas.model.Producto;
import com.chifawufu.sistema_ventas.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
// LA LÍNEA @CrossOrigin SE HA ELIMINADO
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    @PreAuthorize("hasRole('Administrador')")
    public List<Producto> getAllProductos() {
        return productoService.findAll();
    }
    
    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('Administrador', 'Cajero', 'Mozo')")
    public List<Producto> getProductosActivos() {
        return productoService.findActivos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Administrador', 'Cajero', 'Mozo')")
    public ResponseEntity<Producto> getProductoById(@PathVariable @NonNull Long id) {
        Optional<Producto> producto = productoService.findById(id);
        return producto.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('Administrador')")
    public Producto createProducto(@RequestBody @NonNull Producto producto) {
        return productoService.save(producto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<Producto> updateProducto(@PathVariable @NonNull Long id, @RequestBody @NonNull Producto productoDetails) {
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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<Void> deleteProducto(@PathVariable @NonNull Long id) {
        if (productoService.findById(id).isPresent()) {
            productoService.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}