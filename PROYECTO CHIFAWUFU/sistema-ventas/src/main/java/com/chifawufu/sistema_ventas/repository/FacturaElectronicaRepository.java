package com.chifawufu.sistema_ventas.repository;

import com.chifawufu.sistema_ventas.model.FacturaElectronica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaElectronicaRepository extends JpaRepository<FacturaElectronica, Long> {
}