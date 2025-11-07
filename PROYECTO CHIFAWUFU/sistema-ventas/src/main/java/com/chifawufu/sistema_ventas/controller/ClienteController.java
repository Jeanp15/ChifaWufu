package com.chifawufu.sistema_ventas.controller;

import com.chifawufu.sistema_ventas.model.Cliente;
import com.chifawufu.sistema_ventas.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "http://localhost:3000")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // --- Endpoints CRUD para Clientes (RF08) ---
    // Protegido para los roles que atienden al público

    // Obtener todos los clientes
    @GetMapping
    @PreAuthorize("hasAnyRole('Cajero', 'Mozo', 'Administrador')")
    public List<Cliente> getAllClientes() {
        return clienteService.findAll();
    }

    // Obtener un solo cliente por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Cajero', 'Mozo', 'Administrador')")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteService.findById(id);
        return cliente.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    // Registrar un nuevo cliente
    @PostMapping
    @PreAuthorize("hasAnyRole('Cajero', 'Mozo', 'Administrador')")
    public Cliente createCliente(@RequestBody Cliente cliente) {
        return clienteService.save(cliente);
    }

    // Actualizar un cliente existente (RF08)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('Cajero', 'Mozo', 'Administrador')")
    public ResponseEntity<Cliente> updateCliente(@PathVariable Long id, @RequestBody Cliente clienteDetails) {
        Optional<Cliente> clienteOptional = clienteService.findById(id);

        if (clienteOptional.isPresent()) {
            Cliente clienteExistente = clienteOptional.get();
            clienteExistente.setNombre(clienteDetails.getNombre());
            clienteExistente.setDni(clienteDetails.getDni());
            clienteExistente.setTelefono(clienteDetails.getTelefono());
            clienteExistente.setDireccion(clienteDetails.getDireccion());
            clienteExistente.setCorreo(clienteDetails.getCorreo());
            
            return ResponseEntity.ok(clienteService.save(clienteExistente));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Borrar un cliente (Solo Admin, por seguridad)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        if (clienteService.findById(id).isPresent()) {
            clienteService.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}