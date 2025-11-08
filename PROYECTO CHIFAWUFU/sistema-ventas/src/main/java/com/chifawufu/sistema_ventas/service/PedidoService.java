package com.chifawufu.sistema_ventas.service;

import com.chifawufu.sistema_ventas.dto.DetallePedidoDTO;
import com.chifawufu.sistema_ventas.dto.PedidoRequestDTO;
import com.chifawufu.sistema_ventas.model.*;
import com.chifawufu.sistema_ventas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.lang.NonNull; 
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects; 

@Service
public class PedidoService {

    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private DetallePedidoRepository detallePedidoRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ClienteRepository clienteRepository;
    
    @Transactional
    public Pedido crearPedido(@NonNull PedidoRequestDTO request) {
        
        String nombreUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. CORRECCIÓN DE LÓGICA (PARA EVITAR NullPointerException)
        Cliente cliente = null; 
        if (request.idCliente() != null) { 
            
            // ¡ESTA ES LA LÍNEA MÁGICA!
            // Le decimos a Java: "Estoy seguro de que esto no es nulo"
            Long clienteIdValidado = Objects.requireNonNull(request.idCliente());

            cliente = clienteRepository.findById(clienteIdValidado)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + clienteIdValidado));
        }

        // 3. Crear la cabecera del Pedido
        Pedido pedido = new Pedido(request.tipo(), request.mesa(), cliente, usuario);
        pedido = pedidoRepository.save(pedido);

        List<DetallePedido> detallesGuardados = new ArrayList<>();
        BigDecimal totalPedido = BigDecimal.ZERO;

        for (DetallePedidoDTO detalleDTO : request.detalles()) {
            
            // Corrección de Null Safety
            Long idProducto = Objects.requireNonNull(detalleDTO.idProducto(), "El ID del producto en el detalle no puede ser nulo");
            
            Producto producto = productoRepository.findById(idProducto)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (producto.getStock() < detalleDTO.cantidad()) {
                throw new RuntimeException("No hay stock suficiente para: " + producto.getNombre());
            }

            DetallePedido detalle = new DetallePedido(pedido, producto, detalleDTO.cantidad());
            detallesGuardados.add(detalle);
            
            producto.setStock(producto.getStock() - detalleDTO.cantidad());
            productoRepository.save(producto);
            
            totalPedido = totalPedido.add(detalle.getSubtotal());
        }

        detallePedidoRepository.saveAll(detallesGuardados);
        pedido.setDetalles(detallesGuardados);
        pedido.setTotal(totalPedido);
        
        // Esta línea también tenía una advertencia, pero se soluciona
        // al corregir los nulos de arriba.
        return pedidoRepository.save(pedido); 
    }

    public List<Pedido> findByEstado(@NonNull String estado) {
        return pedidoRepository.findByEstado(estado);
    }

    public Pedido actualizarEstado(@NonNull Long idPedido, @NonNull String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }
}