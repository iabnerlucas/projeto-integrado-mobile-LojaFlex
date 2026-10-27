package com.devgabriel.lojaflex;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.devgabriel.lojaflex.model.Empresa;
import com.devgabriel.lojaflex.model.PerfilEnum;
import com.devgabriel.lojaflex.model.Produto;
import com.devgabriel.lojaflex.model.Usuario;
import com.devgabriel.lojaflex.repository.EmpresaRepository;
import com.devgabriel.lojaflex.repository.ProdutoRepository;
import com.devgabriel.lojaflex.repository.UsuarioRepository;

@SpringBootApplication
public class LojaflexBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LojaflexBackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner cargaInicial(UsuarioRepository usuarioRepo, ProdutoRepository produtoRepo,
			EmpresaRepository empresaRepo) {
		return args -> {
			if (usuarioRepo.count() == 0) {
				Empresa emp = new Empresa();
				emp.setNomeFantasia("Loja Flex Modelo");
				emp.setStatusAprovacao("APROVADO");
				empresaRepo.save(emp);

				Usuario admin = new Usuario();
				admin.setNome("Administrador");
				admin.setLogin("admin");
				admin.setSenha("123456");
				admin.setPerfil(PerfilEnum.GERENTE_LOJA);
				admin.setEmpresa(emp);
				usuarioRepo.save(admin);

				Usuario op = new Usuario();
				op.setNome("Operador Padrão");
				op.setLogin("operador");
				op.setSenha("123456");
				op.setPerfil(PerfilEnum.OPERADOR);
				op.setEmpresa(emp);
				usuarioRepo.save(op);

				Produto p1 = new Produto();
				p1.setNome("Arroz 5kg");
				p1.setCodigoBarras("7891234567890");
				p1.setPreco(new BigDecimal("29.90"));
				p1.setEstoque(30);
				p1.setEmpresa(emp);
				produtoRepo.save(p1);

				Produto p2 = new Produto();
				p2.setNome("Feijão Preto 1kg");
				p2.setCodigoBarras("7891234567891");
				p2.setPreco(new BigDecimal("8.50"));
				p2.setEstoque(3);
				p2.setEmpresa(emp);
				produtoRepo.save(p2);
			}
		};
	}

}
