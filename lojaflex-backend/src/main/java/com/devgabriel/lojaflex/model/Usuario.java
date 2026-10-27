package com.devgabriel.lojaflex.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_usuarios")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nome;

	@Column(nullable = false, unique = true)
	private String login;

	@Column(nullable = false)
	private String senha;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PerfilEnum perfil;

	@ManyToOne
	@JoinColumn(name = "empresa_id")
	private Empresa empresa;

	@Column(name = "status_aprovacao", nullable = false)
	private String statusAprovacao = "PENDENTE"; // PENDENTE, APROVADO, REJEITADO

	public Usuario() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public PerfilEnum getPerfil() {
		return perfil;
	}

	public void setPerfil(PerfilEnum perfil) {
		this.perfil = perfil;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public String getStatusAprovacao() {
		return statusAprovacao;
	}

	public void setStatusAprovacao(String statusAprovacao) {
		this.statusAprovacao = statusAprovacao;
	}
}