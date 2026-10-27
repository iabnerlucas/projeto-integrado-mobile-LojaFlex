package com.devgabriel.lojaflex.dto;

import com.devgabriel.lojaflex.model.PerfilEnum;

public class CadastroRequest {
	private String nome;
	private String login;
	private String senha;
	private PerfilEnum perfil;

	public CadastroRequest() {
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
}