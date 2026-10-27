package com.devgabriel.lojaflex.repository;

import com.devgabriel.lojaflex.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	Optional<Usuario> findByLogin(String login);
	List<Usuario> findByEmpresaIdAndStatusAprovacao(Long empresaId, String statusAprovacao);
	List<Usuario> findByStatusAprovacao(String statusAprovacao);
}