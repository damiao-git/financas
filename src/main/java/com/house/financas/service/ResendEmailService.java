package com.house.financas.service;

import com.house.financas.exception.DomainException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ResendEmailService implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResendEmailService.class);
    private static final String RESEND_EMAILS_URL = "https://api.resend.com/emails";

    private final RestClient restClient;
    private final String apiKey;
    private final String from;
    private final boolean enabled;

    public ResendEmailService(
            RestClient.Builder restClientBuilder,
            @Value("${monexa.email.resend.api-key:}") String apiKey,
            @Value("${monexa.email.from:Monexa <onboarding@resend.dev>}") String from,
            @Value("${monexa.email.enabled:false}") boolean enabled) {
        this.restClient = restClientBuilder.build();
        this.apiKey = apiKey;
        this.from = from;
        this.enabled = enabled;
    }

    @Override
    public void enviarResetSenha(String destinatario, String nome, String resetLink) {
        if (!enabled || apiKey == null || apiKey.isBlank()) {
            LOGGER.info("Reset de senha solicitado para {}. Link: {}", destinatario, resetLink);
            return;
        }

        try {
            restClient.post()
                    .uri(RESEND_EMAILS_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .body(new ResendEmailRequest(
                            from,
                            List.of(destinatario),
                            "Redefina sua senha da Monexa",
                            montarHtml(nome, resetLink),
                            montarTexto(resetLink)
                    ))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException exception) {
            LOGGER.error(
                    "Erro ao enviar email pelo Resend. Status: {}. Resposta: {}",
                    exception.getStatusCode(),
                    exception.getResponseBodyAsString()
            );
            throw new DomainException("Nao foi possivel enviar o email de recuperacao");
        } catch (Exception exception) {
            LOGGER.error("Erro inesperado ao enviar email pelo Resend", exception);
            throw new DomainException("Nao foi possivel enviar o email de recuperacao");
        }
    }

    private String montarHtml(String nome, String resetLink) {
        return """
                <p>Ola, %s.</p>
                <p>Recebemos uma solicitacao para redefinir sua senha da Monexa.</p>
                <p><a href="%s">Clique aqui para criar uma nova senha</a>.</p>
                <p>Este link expira em 30 minutos. Se voce nao solicitou, ignore este email.</p>
                """.formatted(nome, resetLink);
    }

    private String montarTexto(String resetLink) {
        return "Recebemos uma solicitacao para redefinir sua senha da Monexa. "
                + "Acesse este link para criar uma nova senha: " + resetLink
                + " Este link expira em 30 minutos.";
    }

    private record ResendEmailRequest(
            String from,
            List<String> to,
            String subject,
            String html,
            String text
    ) {}
}
