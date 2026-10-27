package com.devgabriel.lojaflex.repository;

import com.devgabriel.lojaflex.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByEmpresaId(Long empresaId);
    List<Produto> findByEmpresaIdAndNomeContainingIgnoreCase(Long empresaId, String nome);
    long countByEmpresaIdAndEstoqueLessThanEqual(Long empresaId, Integer limite);
}