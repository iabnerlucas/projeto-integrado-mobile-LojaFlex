package com.devgabriel.lojaflex.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devgabriel.lojaflex.dto.CadastroEmpresaRequest;
import com.devgabriel.lojaflex.dto.CadastroRequest;
import com.devgabriel.lojaflex.dto.DashboardMetricas;
import com.devgabriel.lojaflex.dto.ItemPedidoRequest;
import com.devgabriel.lojaflex.dto.LoginRequest;
import com.devgabriel.lojaflex.dto.LoginResponse;
import com.devgabriel.lojaflex.dto.PedidoRequest;
import com.devgabriel.lojaflex.dto.PedidoResponse;
import com.devgabriel.lojaflex.model.ClienteFiado;
import com.devgabriel.lojaflex.model.ContaPagar;
import com.devgabriel.lojaflex.model.Empresa;
import com.devgabriel.lojaflex.model.Fornecedor;
import com.devgabriel.lojaflex.model.ItemPedido;
import com.devgabriel.lojaflex.model.Pedido;
import com.devgabriel.lojaflex.model.PerfilEnum;
import com.devgabriel.lojaflex.model.Produto;
import com.devgabriel.lojaflex.model.Usuario;
import com.devgabriel.lojaflex.repository.ClienteFiadoRepository;
import com.devgabriel.lojaflex.repository.ContaPagarRepository;
import com.devgabriel.lojaflex.repository.EmpresaRepository;
import com.devgabriel.lojaflex.repository.FornecedorRepository;
import com.devgabriel.lojaflex.repository.PedidoRepository;
import com.devgabriel.lojaflex.repository.ProdutoRepository;
import com.devgabriel.lojaflex.repository.UsuarioRepository;

@Service
public class LojaService {

	private final UsuarioRepository usuarioRepository;
	private final ProdutoRepository produtoRepository;
	private final PedidoRepository pedidoRepository;
	private final EmpresaRepository empresaRepository;
	private final ClienteFiadoRepository clienteFiadoRepository;
	private final ContaPagarRepository contaPagarRepository;
	private final FornecedorRepository fornecedorRepository;

	public LojaService(UsuarioRepository usuarioRepository, ProdutoRepository produtoRepository,
			PedidoRepository pedidoRepository, EmpresaRepository empresaRepository,
			ClienteFiadoRepository clienteFiadoRepository, ContaPagarRepository contaPagarRepository,
			FornecedorRepository fornecedorRepository) {
		this.usuarioRepository = usuarioRepository;
		this.produtoRepository = produtoRepository;
		this.pedidoRepository = pedidoRepository;
		this.empresaRepository = empresaRepository;
		this.clienteFiadoRepository = clienteFiadoRepository;
		this.contaPagarRepository = contaPagarRepository;
		this.fornecedorRepository = fornecedorRepository;
	}

	public LoginResponse autenticar(LoginRequest request) {
		Usuario usuario = usuarioRepository.findByLogin(request.getLogin())
				.orElseThrow(() -> new RuntimeException("Usuário ou senha inválidos."));

		if (!usuario.getSenha().equals(request.getSenha())) {
			throw new RuntimeException("Usuário ou senha inválidos.");
		}

		// Bloqueia se o registo ainda estiver pendente de aprovação
		if ("PENDENTE".equalsIgnoreCase(usuario.getStatusAprovacao())) {
			throw new RuntimeException("Seu cadastro está aguardando aprovação do administrador.");
		}

		if ("REJEITADO".equalsIgnoreCase(usuario.getStatusAprovacao())) {
			throw new RuntimeException("Acesso negado pelo administrador.");
		}

		String token = UUID.randomUUID().toString();
		return new LoginResponse(token, usuario);
	}

	public Usuario cadastrarUsuario(CadastroRequest request, Long empresaId) {
		Usuario usuario = new Usuario();
		usuario.setNome(request.getNome());
		usuario.setLogin(request.getLogin());
		usuario.setSenha(request.getSenha());
		usuario.setPerfil(request.getPerfil() != null ? request.getPerfil() : PerfilEnum.OPERADOR);
		usuario.setStatusAprovacao("PENDENTE"); // Salvo como PENDENTE por defeito

		if (empresaId != null && empresaId > 0) {
			Empresa empresa = empresaRepository.findById(empresaId)
					.orElseThrow(() -> new RuntimeException("Empresa não encontrada."));
			usuario.setEmpresa(empresa);
		}
		return usuarioRepository.save(usuario);
	}

	public List<Usuario> listarUsuariosPendentes(Long empresaId) {
		if (empresaId != null && empresaId > 0) {
			return usuarioRepository.findByEmpresaIdAndStatusAprovacao(empresaId, "PENDENTE");
		}
		return usuarioRepository.findByStatusAprovacao("PENDENTE");
	}

	public Usuario aprovarUsuario(Long id) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
		usuario.setStatusAprovacao("APROVADO");
		return usuarioRepository.save(usuario);
	}

	public Usuario rejeitarUsuario(Long id) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
		usuario.setStatusAprovacao("REJEITADO");
		return usuarioRepository.save(usuario);
	}

	public Empresa criarEmpresaCompleta(CadastroEmpresaRequest request) {
		Empresa empresa = new Empresa();
		empresa.setNomeFantasia(request.getNomeFantasia());
		empresa.setRazaoSocial(request.getRazaoSocial());
		empresa.setCnpj(request.getCnpj());
		empresa.setTelefone(request.getTelefone());
		empresa.setStatusAprovacao("APROVADO");
		empresa = empresaRepository.save(empresa);

		if (request.getEmailAdmin() != null && !request.getEmailAdmin().isEmpty()) {
			Usuario admin = new Usuario();
			admin.setNome(request.getNomeAdmin() != null ? request.getNomeAdmin() : "Admin " + empresa.getNomeFantasia());
			admin.setLogin(request.getEmailAdmin());
			admin.setSenha(request.getSenhaAdmin() != null ? request.getSenhaAdmin() : "123456");
			admin.setPerfil(PerfilEnum.GERENTE_LOJA); // Utiliza PerfilEnum.ADMIN
			admin.setStatusAprovacao("APROVADO"); // O criador da empresa já fica aprovado
			admin.setEmpresa(empresa);
			usuarioRepository.save(admin);
		}
		return empresa;
	}

	public Empresa solicitarCadastroEmpresa(CadastroEmpresaRequest request) {
		Empresa empresa = new Empresa();
		empresa.setNomeFantasia(request.getNomeFantasia());
		empresa.setRazaoSocial(request.getRazaoSocial());
		empresa.setCnpj(request.getCnpj());
		empresa.setTelefone(request.getTelefone());
		empresa.setStatusAprovacao("PENDENTE");
		return empresaRepository.save(empresa);
	}

	public List<Produto> listarProdutos(Long empresaId, String busca) {
		if (busca != null && !busca.trim().isEmpty()) {
			return produtoRepository.findByEmpresaIdAndNomeContainingIgnoreCase(empresaId, busca);
		}
		return produtoRepository.findByEmpresaId(empresaId);
	}

	public Produto salvarProduto(Long empresaId, Produto produto) {
		Empresa empresa = empresaRepository.findById(empresaId)
				.orElseThrow(() -> new RuntimeException("Empresa não encontrada."));
		produto.setEmpresa(empresa);
		return produtoRepository.save(produto);
	}

	public Produto ajustarEstoque(Long produtoId, Integer novoEstoque) {
		Produto produto = produtoRepository.findById(produtoId)
				.orElseThrow(() -> new RuntimeException("Produto não encontrado."));
		produto.setEstoque(novoEstoque);
		return produtoRepository.save(produto);
	}

	@Transactional
	public PedidoResponse processarPedido(Long empresaId, PedidoRequest request) {
		Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
				.orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
		Empresa empresa = empresaRepository.findById(empresaId)
				.orElseThrow(() -> new RuntimeException("Empresa não encontrada."));

		Pedido pedido = new Pedido();
		pedido.setUsuario(usuario);
		pedido.setEmpresa(empresa);
		pedido.setFormaPagamento(request.getFormaPagamento());
		pedido.setDataHora(LocalDateTime.now());

		BigDecimal total = BigDecimal.ZERO;

		for (ItemPedidoRequest itemReq : request.getItens()) {
			Produto produto = produtoRepository.findById(itemReq.getProdutoId()).orElseThrow(
					() -> new RuntimeException("Produto ID " + itemReq.getProdutoId() + " não encontrado."));

			if (produto.getEstoque() < itemReq.getQuantidade()) {
				throw new RuntimeException("Estoque insuficiente para: " + produto.getNome());
			}

			produto.setEstoque(produto.getEstoque() - itemReq.getQuantidade());
			produtoRepository.save(produto);

			ItemPedido item = new ItemPedido();
			item.setPedido(pedido);
			item.setProduto(produto);
			item.setQuantidade(itemReq.getQuantidade());
			item.setPrecoUnitario(produto.getPreco());

			BigDecimal subtotal = produto.getPreco().multiply(BigDecimal.valueOf(itemReq.getQuantidade()));
			total = total.add(subtotal);

			pedido.getItens().add(item);
		}

		pedido.setValorTotal(total);
		Pedido salvo = pedidoRepository.save(pedido);

		return new PedidoResponse(salvo.getId(), "CONCLUIDO", total);
	}

	public DashboardMetricas obterMetricasDashboard(Long empresaId) {
		LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
		LocalDateTime fimDia = LocalDate.now().atTime(LocalTime.MAX);

		BigDecimal totalHoje = pedidoRepository.sumTotalByEmpresaAndDataHoraBetween(empresaId, inicioDia, fimDia);
		Integer qtdVendas = pedidoRepository.countByEmpresaAndDataHoraBetween(empresaId, inicioDia, fimDia);
		Integer estoqueBaixo = (int) produtoRepository.countByEmpresaIdAndEstoqueLessThanEqual(empresaId, 5);
		BigDecimal totalContas = contaPagarRepository.sumTotalPendenteByEmpresa(empresaId);

		return new DashboardMetricas(totalHoje, qtdVendas, estoqueBaixo, totalContas);
	}

	public List<ClienteFiado> listarClientesFiado(Long empresaId) {
		return clienteFiadoRepository.findByEmpresaId(empresaId);
	}

	public ClienteFiado salvarClienteFiado(Long empresaId, ClienteFiado cliente) {
		Empresa empresa = empresaRepository.findById(empresaId)
				.orElseThrow(() -> new RuntimeException("Empresa não encontrada."));
		cliente.setEmpresa(empresa);
		return clienteFiadoRepository.save(cliente);
	}

	public List<ContaPagar> listarContasPagar(Long empresaId) {
		return contaPagarRepository.findByEmpresaId(empresaId);
	}

	public ContaPagar salvarContaPagar(Long empresaId, ContaPagar conta) {
		Empresa empresa = empresaRepository.findById(empresaId)
				.orElseThrow(() -> new RuntimeException("Empresa não encontrada."));
		conta.setEmpresa(empresa);
		return contaPagarRepository.save(conta);
	}

	public ContaPagar darBaixaConta(Long id) {
		ContaPagar conta = contaPagarRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Conta não encontrada."));
		conta.setPago(true);
		return contaPagarRepository.save(conta);
	}

	public List<Fornecedor> listarFornecedores(Long empresaId) {
		return fornecedorRepository.findByEmpresaId(empresaId);
	}

	public Fornecedor salvarFornecedor(Long empresaId, Fornecedor fornecedor) {
		Empresa empresa = empresaRepository.findById(empresaId)
				.orElseThrow(() -> new RuntimeException("Empresa não encontrada."));
		fornecedor.setEmpresa(empresa);
		return fornecedorRepository.save(fornecedor);
	}

	public List<Empresa> listarLojasPendentes() {
		return empresaRepository.findByStatusAprovacao("PENDENTE");
	}

	public Empresa aprovarLoja(Long id) {
		Empresa empresa = empresaRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Empresa não encontrada."));
		empresa.setStatusAprovacao("APROVADO");
		return empresaRepository.save(empresa);
	}
}