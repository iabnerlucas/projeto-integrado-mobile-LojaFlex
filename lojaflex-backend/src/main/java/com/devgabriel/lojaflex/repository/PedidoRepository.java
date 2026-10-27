package com.devgabriel.lojaflex.repository;

import com.devgabriel.lojaflex.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    @Query("SELECT COALESCE(SUM(p.valorTotal), 0) FROM Pedido p WHERE p.empresa.id = :empresaId AND p.dataHora BETWEEN :inicio AND :fim")
    BigDecimal sumTotalByEmpresaAndDataHoraBetween(@Param("empresaId") Long empresaId, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.empresa.id = :empresaId AND p.dataHora BETWEEN :inicio AND :fim")
    Integer countByEmpresaAndDataHoraBetween(@Param("empresaId") Long empresaId, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}