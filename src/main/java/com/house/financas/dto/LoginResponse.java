package com.house.financas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private String token;
    private String tipo = "Bearer";
    private Boolean trocarSenhaNoProximoLogin = false;

    public LoginResponse(String token) {
        this.token = token;
    }

    public LoginResponse(String token, Boolean trocarSenhaNoProximoLogin) {
        this.token = token;
        this.trocarSenhaNoProximoLogin = Boolean.TRUE.equals(trocarSenhaNoProximoLogin);
    }
}
