package com.chifawufu.sistema_ventas.controller;

import com.chifawufu.sistema_ventas.model.Cliente;
import com.chifawufu.sistema_ventas.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
// LA LÍNEA @CrossOrigin SE HA ELIMINADO
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    @PreAuthorize("hasAnyRole('Cajero', 'Mozo', 'Administrador')")
    public List<Cliente> getAllClientes() {
        return clienteService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Cajero', 'Mozo', 'Administrador')")
    public ResponseEntity<Cliente> getClienteById(@PathVariable @NonNull Long id) {
        Optional<Cliente> cliente = clienteService.findById(id);
        return cliente.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('Cajero', 'Mozo', 'Administrador')")
    public Cliente createCliente(@RequestBody @NonNull Cliente cliente) {
        return clienteService.save(cliente);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('Cajero', 'Mozo', 'Administrador')")
    public ResponseEntity<Cliente> updateCliente(@PathVariable @NonNull Long id, @RequestBody @NonNull Cliente clienteDetails) {
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

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('Administrador')")
    public ResponseEntity<Void> deleteCliente(@PathVariable @NonNull Long id) {
        if (clienteService.findById(id).isPresent()) {
            clienteService.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}