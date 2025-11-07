package com.chifawufu.sistema_ventas.repository;

import com.chifawufu.sistema_ventas.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}
