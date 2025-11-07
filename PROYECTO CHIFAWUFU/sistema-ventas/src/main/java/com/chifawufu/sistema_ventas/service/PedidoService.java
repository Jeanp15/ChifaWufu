package com.chifawufu.sistema_ventas.service;

import com.chifawufu.sistema_ventas.dto.DetallePedidoDTO;
import com.chifawufu.sistema_ventas.dto.PedidoRequestDTO;
import com.chifawufu.sistema_ventas.model.*;
import com.chifawufu.sistema_ventas.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private DetallePedidoRepository detallePedidoRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private ClienteRepository clienteRepository; // Asumiremos que crearás este repositorio

    // @Transactional asegura que si algo falla (ej. no hay stock),
    // no se guarde NADA en la base de datos. O todo o nada.
    @Transactional
    public Pedido crearPedido(PedidoRequestDTO request) {
        
        // 1. Obtener el usuario (Mozo/Cajero) que está logueado
        String nombreUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Obtener el cliente
        // (Nota: necesitarás crear ClienteRepository igual que hiciste con los otros)
        // Cliente cliente = clienteRepository.findById(request.idCliente())
        //         .orElse(null); // Permitir pedidos sin cliente registrado

        // Por ahora, lo dejaremos simple sin el cliente:
         Cliente cliente = null;
         if (request.idCliente() != null) {
            // Aquí iría la lógica para buscarlo con clienteRepository
         }

        // 3. Crear la cabecera del Pedido
        Pedido pedido = new Pedido(request.tipo(), request.mesa(), cliente, usuario);
        pedido = pedidoRepository.save(pedido); // Guardamos para obtener el ID

        List<DetallePedido> detallesGuardados = new ArrayList<>();
        BigDecimal totalPedido = BigDecimal.ZERO;

        // 4. Recorrer los productos del pedido
        for (DetallePedidoDTO detalleDTO : request.detalles()) {
            
            Producto producto = productoRepository.findById(detalleDTO.idProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // 5. ¡Validar Stock!
            if (producto.getStock() < detalleDTO.cantidad()) {
                throw new RuntimeException("No hay stock suficiente para: " + producto.getNombre());
            }

            // 6. Crear el detalle del pedido
            DetallePedido detalle = new DetallePedido(pedido, producto, detalleDTO.cantidad());
            detallesGuardados.add(detalle);
            
            // 7. Actualizar el stock del producto
            producto.setStock(producto.getStock() - detalleDTO.cantidad());
            productoRepository.save(producto);
            
            totalPedido = totalPedido.add(detalle.getSubtotal());
        }

        // 8. Guardar todos los detalles y actualizar el total del pedido
        detallePedidoRepository.saveAll(detallesGuardados);
        pedido.setDetalles(detallesGuardados);
        pedido.setTotal(totalPedido);
        
        return pedidoRepository.save(pedido);
    }
}