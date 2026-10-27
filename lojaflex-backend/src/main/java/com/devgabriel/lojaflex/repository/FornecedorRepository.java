package com.devgabriel.lojaflex.repository;

import com.devgabriel.lojaflex.model.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    List<Fornecedor> findByEmpresaId(Long empresaId);
}