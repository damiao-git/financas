package com.house.financas.service;

import com.house.financas.exception.DomainException;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GoogleAuthService {

    private static final String TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo?id_token={idToken}";

    private final RestClient restClient;
    private final String clientId;

    public GoogleAuthService(
            RestClient.Builder restClientBuilder,
            @Value("${monexa.google.client-id:}") String clientId) {
        this.restClient = restClientBuilder.build();
        this.clientId = clientId;
    }

    public GoogleUserInfo validarToken(String idToken) {
        if (clientId == null || clientId.isBlank()) {
            throw new DomainException("Login com Google não configurado");
        }

        GoogleTokenInfo tokenInfo;
        try {
            tokenInfo = restClient.get()
                    .uri(TOKEN_INFO_URL, idToken)
                    .retrieve()
                    .body(GoogleTokenInfo.class);
        } catch (Exception exception) {
            throw new DomainException("Token do Google inválido");
        }

        if (tokenInfo == null || tokenInfo.email() == null || tokenInfo.email().isBlank()) {
            throw new DomainException("Token do Google inválido");
        }

        if (!clientId.equals(tokenInfo.audience())) {
            throw new DomainException("Token do Google emitido para outro aplicativo");
        }

        if (!Boolean.parseBoolean(tokenInfo.emailVerified())) {
            throw new DomainException("E-mail do Google não verificado");
        }

        String nome = tokenInfo.name() == null || tokenInfo.name().isBlank()
                ? tokenInfo.email()
                : tokenInfo.name();

        return new GoogleUserInfo(nome, tokenInfo.email());
    }

    private record GoogleTokenInfo(
            String email,
            String name,
            @JsonProperty("aud") String audience,
            @JsonProperty("email_verified") String emailVerified
    ) {}

    public record GoogleUserInfo(String nome, String email) {}
}
