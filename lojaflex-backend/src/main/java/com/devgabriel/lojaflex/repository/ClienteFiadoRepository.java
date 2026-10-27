package com.devgabriel.lojaflex.repository;

import com.devgabriel.lojaflex.model.ClienteFiado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClienteFiadoRepository extends JpaRepository<ClienteFiado, Long> {
    List<ClienteFiado> findByEmpresaId(Long empresaId);
}