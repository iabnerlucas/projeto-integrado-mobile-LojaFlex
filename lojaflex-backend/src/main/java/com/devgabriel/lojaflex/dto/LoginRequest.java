package com.devgabriel.lojaflex.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest {

    @JsonProperty("login")
    @JsonAlias({"usuario", "email", "username"})
    private String login;

    @JsonProperty("senha")
    @JsonAlias({"password"})
    private String senha;

    public LoginRequest() {
    }

    public LoginRequest(String login, String senha) {
        this.login = login;
        this.senha = senha;
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
}