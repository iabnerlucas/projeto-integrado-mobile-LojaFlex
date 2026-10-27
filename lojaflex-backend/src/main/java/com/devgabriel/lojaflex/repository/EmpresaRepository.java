package com.devgabriel.lojaflex.repository;

import com.devgabriel.lojaflex.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
	List<Empresa> findByStatusAprovacao(String statusAprovacao);
}