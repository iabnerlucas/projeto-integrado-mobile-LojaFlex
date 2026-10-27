package com.devgabriel.lojaflex.repository;

import com.devgabriel.lojaflex.model.ContaPagar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface ContaPagarRepository extends JpaRepository<ContaPagar, Long> {
    List<ContaPagar> findByEmpresaId(Long empresaId);

    @Query("SELECT COALESCE(SUM(c.valor), 0) FROM ContaPagar c WHERE c.empresa.id = :empresaId AND c.pago = false")
    BigDecimal sumTotalPendenteByEmpresa(@Param("empresaId") Long empresaId);
}