package com.chifawufu.sistema_ventas.service;

import com.chifawufu.sistema_ventas.model.Cliente;
import com.chifawufu.sistema_ventas.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.lang.NonNull;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    // Obtener todos los clientes
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    // Obtener un cliente por su ID
    public Optional<Cliente> findById(@NonNull Long id) {
        return clienteRepository.findById(id);
    }
    
    public Cliente save(@NonNull Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void deleteById(@NonNull Long id) {
        clienteRepository.deleteById(id);
    }
    
    // (Opcional: podrías añadir un método para buscar por DNI/RUC)
}