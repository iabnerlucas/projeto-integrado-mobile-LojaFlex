package com.devgabriel.lojaflex.dto;

import com.devgabriel.lojaflex.model.Usuario;

public class LoginResponse {
	private String token;
	private Usuario usuario;

	public LoginResponse(String token, Usuario usuario) {
		this.token = token;
		this.usuario = usuario;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}