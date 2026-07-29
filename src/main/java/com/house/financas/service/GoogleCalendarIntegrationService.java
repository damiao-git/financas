package com.house.financas.service;

import com.house.financas.dto.GoogleCalendarAuthUrlResponse;
import com.house.financas.dto.GoogleCalendarStatusResponse;
import com.house.financas.exception.DomainException;
import com.house.financas.model.ContaMensal;
import com.house.financas.model.GoogleCalendarIntegration;
import com.house.financas.model.Usuario;
import com.house.financas.model.enums.StatusContaMensal;
import com.house.financas.repository.ContaMensalRepository;
import com.house.financas.repository.GoogleCalendarIntegrationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class GoogleCalendarIntegrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleCalendarIntegrationService.class);
    private static final String AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String EVENTS_URL = "https://www.googleapis.com/calendar/v3/calendars/{calendarId}/events";

    private final GoogleCalendarIntegrationRepository integrationRepository;
    private final ContaMensalRepository contaMensalRepository;
    private final RestClient.Builder restClientBuilder;

    @Value("${monexa.google.client-id:}")
    private String clientId;

    @Value("${monexa.google.client-secret:}")
    private String clientSecret;

    @Value("${monexa.google.calendar.redirect-uri:http://localhost:8080/integracoes/google-calendar/callback}")
    private String redirectUri;

    @Value("${monexa.frontend.integracoes-url:http://localhost:4200/integracoes}")
    private String frontendIntegracoesUrl;

    public GoogleCalendarStatusResponse status(Usuario usuario) {
        return integrationRepository.findByUsuarioId(usuario.getId())
                .filter(integration -> Boolean.TRUE.equals(integration.getConectado()))
                .map(integration -> new GoogleCalendarStatusResponse(
                        true,
                        integration.getCalendarId(),
                        integration.getConectadoEm()
                ))
                .orElse(new GoogleCalendarStatusResponse(false, "primary", null));
    }

    public GoogleCalendarAuthUrlResponse iniciarAutorizacao(Usuario usuario) {
        validarConfiguracao();

        GoogleCalendarIntegration integration = integrationRepository.findByUsuarioId(usuario.getId())
                .orElseGet(() -> novaIntegracao(usuario));
        String state = UUID.randomUUID().toString();

        integration.setAuthState(state);
        integrationRepository.save(integration);

        String authorizationUrl = UriComponentsBuilder
                .fromUriString(AUTH_URL)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "https://www.googleapis.com/auth/calendar.events")
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .queryParam("state", state)
                .build()
                .toUriString();

        return new GoogleCalendarAuthUrlResponse(authorizationUrl);
    }

    public URI concluirAutorizacao(String code, String state) {
        validarConfiguracao();

        if (code == null || code.isBlank() || state == null || state.isBlank()) {
            return redirect("googleCalendar=erro");
        }

        GoogleCalendarIntegration integration = integrationRepository.findByAuthState(state)
                .orElseThrow(() -> new DomainException("Autorização do Google Agenda inválida"));

        GoogleTokenResponse token = trocarCodigoPorToken(code);
        integration.setAccessToken(token.accessToken());
        if (token.refreshToken() != null && !token.refreshToken().isBlank()) {
            integration.setRefreshToken(token.refreshToken());
        }
        integration.setAccessTokenExpiraEm(LocalDateTime.now().plusSeconds(token.expiresIn()));
        integration.setConectado(true);
        integration.setCalendarId("primary");
        integration.setConectadoEm(LocalDateTime.now());
        integration.setAuthState(null);
        integrationRepository.save(integration);
        sincronizarContasPendentes(integration.getUsuario());

        return redirect("googleCalendar=conectado");
    }

    public void desconectar(Usuario usuario) {
        integrationRepository.findByUsuarioId(usuario.getId()).ifPresent(integration -> {
            integration.setConectado(false);
            integration.setAccessToken(null);
            integration.setRefreshToken(null);
            integration.setAccessTokenExpiraEm(null);
            integration.setAuthState(null);
            integrationRepository.save(integration);
        });
    }

    public void sincronizarConta(ContaMensal contaMensal) {
        Optional<GoogleCalendarIntegration> integrationOptional = integracaoConectada(contaMensal.getUsuario());
        if (integrationOptional.isEmpty()) {
            return;
        }

        try {
            GoogleCalendarIntegration integration = integrationOptional.get();
            if (deveManterEvento(contaMensal)) {
                salvarEvento(integration, contaMensal);
            } else {
                removerEvento(integration, contaMensal);
            }
        } catch (Exception exception) {
            LOGGER.warn("Não foi possível sincronizar a conta {} com Google Agenda", contaMensal.getId(), exception);
        }
    }

    private void sincronizarContasPendentes(Usuario usuario) {
        contaMensalRepository.findByUsuarioIdAndAtivoTrueOrderByDataVencimentoAsc(usuario.getId())
                .stream()
                .filter(conta -> StatusContaMensal.PENDENTE.equals(conta.getStatus()))
                .filter(conta -> !conta.getDataVencimento().isBefore(LocalDate.now()))
                .forEach(this::sincronizarConta);
    }

    private GoogleCalendarIntegration novaIntegracao(Usuario usuario) {
        GoogleCalendarIntegration integration = new GoogleCalendarIntegration();
        integration.setUsuario(usuario);
        integration.setConectado(false);
        integration.setCalendarId("primary");
        return integration;
    }

    private Optional<GoogleCalendarIntegration> integracaoConectada(Usuario usuario) {
        return integrationRepository.findByUsuarioId(usuario.getId())
                .filter(integration -> Boolean.TRUE.equals(integration.getConectado()))
                .filter(integration -> integration.getRefreshToken() != null || integration.getAccessToken() != null);
    }

    private boolean deveManterEvento(ContaMensal contaMensal) {
        return Boolean.TRUE.equals(contaMensal.getAtivo())
                && StatusContaMensal.PENDENTE.equals(contaMensal.getStatus())
                && !contaMensal.getDataVencimento().isBefore(LocalDate.now());
    }

    private void salvarEvento(GoogleCalendarIntegration integration, ContaMensal contaMensal) {
        String accessToken = accessTokenValido(integration);
        Map<String, Object> event = montarEvento(contaMensal);

        if (contaMensal.getGoogleCalendarEventId() == null || contaMensal.getGoogleCalendarEventId().isBlank()) {
            GoogleEventResponse response = restClientBuilder.build()
                    .post()
                    .uri(EVENTS_URL, integration.getCalendarId())
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(event)
                    .retrieve()
                    .body(GoogleEventResponse.class);

            if (response != null && response.id() != null) {
                contaMensal.setGoogleCalendarEventId(response.id());
            }
            return;
        }

        restClientBuilder.build()
                .put()
                .uri(EVENTS_URL + "/{eventId}", integration.getCalendarId(), contaMensal.getGoogleCalendarEventId())
                .headers(headers -> headers.setBearerAuth(accessToken))
                .contentType(MediaType.APPLICATION_JSON)
                .body(event)
                .retrieve()
                .toBodilessEntity();
    }

    private void removerEvento(GoogleCalendarIntegration integration, ContaMensal contaMensal) {
        if (contaMensal.getGoogleCalendarEventId() == null || contaMensal.getGoogleCalendarEventId().isBlank()) {
            return;
        }

        try {
            restClientBuilder.build()
                    .delete()
                    .uri(EVENTS_URL + "/{eventId}", integration.getCalendarId(), contaMensal.getGoogleCalendarEventId())
                    .headers(headers -> headers.setBearerAuth(accessTokenValido(integration)))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode().value() != 404 && exception.getStatusCode().value() != 410) {
                throw exception;
            }
        } finally {
            contaMensal.setGoogleCalendarEventId(null);
        }
    }

    private Map<String, Object> montarEvento(ContaMensal contaMensal) {
        LocalDate vencimento = contaMensal.getDataVencimento();
        String valor = formatarValor(contaMensal.getValorPrevisto());
        String categoria = contaMensal.getCategoria() == null ? "Sem categoria" : contaMensal.getCategoria().getNome();

        return Map.of(
                "summary", "Pagar " + contaMensal.getDescricao() + " - " + valor,
                "description", "Conta da Monexa\nCategoria: " + categoria + "\nValor previsto: " + valor,
                "start", Map.of("date", vencimento.toString()),
                "end", Map.of("date", vencimento.plusDays(1).toString()),
                "reminders", Map.of(
                        "useDefault", false,
                        "overrides", List.of(
                                Map.of("method", "popup", "minutes", 1440),
                                Map.of("method", "popup", "minutes", 0)
                        )
                )
        );
    }

    private String accessTokenValido(GoogleCalendarIntegration integration) {
        if (integration.getAccessToken() != null
                && integration.getAccessTokenExpiraEm() != null
                && integration.getAccessTokenExpiraEm().isAfter(LocalDateTime.now().plusMinutes(1))) {
            return integration.getAccessToken();
        }

        if (integration.getRefreshToken() == null || integration.getRefreshToken().isBlank()) {
            throw new DomainException("Google Agenda precisa ser conectado novamente");
        }

        GoogleTokenResponse token = atualizarAccessToken(integration.getRefreshToken());
        integration.setAccessToken(token.accessToken());
        integration.setAccessTokenExpiraEm(LocalDateTime.now().plusSeconds(token.expiresIn()));
        integrationRepository.save(integration);
        return integration.getAccessToken();
    }

    private GoogleTokenResponse trocarCodigoPorToken(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");

        return solicitarToken(body);
    }

    private GoogleTokenResponse atualizarAccessToken(String refreshToken) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);
        body.add("grant_type", "refresh_token");

        return solicitarToken(body);
    }

    private GoogleTokenResponse solicitarToken(MultiValueMap<String, String> body) {
        try {
            GoogleTokenResponse token = restClientBuilder.build()
                    .post()
                    .uri(TOKEN_URL)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .body(GoogleTokenResponse.class);

            if (token == null || token.accessToken() == null || token.accessToken().isBlank()) {
                throw new DomainException("Não foi possível conectar o Google Agenda");
            }

            return token;
        } catch (RestClientResponseException exception) {
            LOGGER.warn("Google retornou erro ao solicitar token: {}", exception.getResponseBodyAsString());
            throw new DomainException("Não foi possível conectar o Google Agenda");
        }
    }

    private URI redirect(String query) {
        return URI.create(frontendIntegracoesUrl + (frontendIntegracoesUrl.contains("?") ? "&" : "?") + query);
    }

    private void validarConfiguracao() {
        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
            throw new DomainException("Google Agenda não configurado");
        }
    }

    private String formatarValor(BigDecimal valor) {
        return "R$ " + valor.setScale(2).toPlainString().replace(".", ",");
    }

    private record GoogleTokenResponse(
            @com.fasterxml.jackson.annotation.JsonProperty("access_token") String accessToken,
            @com.fasterxml.jackson.annotation.JsonProperty("refresh_token") String refreshToken,
            @com.fasterxml.jackson.annotation.JsonProperty("expires_in") Long expiresIn
    ) {
    }

    private record GoogleEventResponse(String id) {
    }
}
