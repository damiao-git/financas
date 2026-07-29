package com.house.financas.service;

import com.house.financas.exception.DomainException;
import com.house.financas.model.PasswordResetToken;
import com.house.financas.model.Usuario;
import com.house.financas.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

@Service
@Transactional
public class PasswordResetService {

    private static final int TOKEN_BYTES = 32;
    private static final int EXPIRATION_MINUTES = 30;

    private final PasswordResetTokenRepository tokenRepository;
    private final UsuarioService usuarioService;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final String resetPasswordUrl;

    public PasswordResetService(
            PasswordResetTokenRepository tokenRepository,
            UsuarioService usuarioService,
            EmailService emailService,
            @Value("${monexa.frontend.reset-password-url:http://localhost:4200/resetar-senha}") String resetPasswordUrl) {
        this.tokenRepository = tokenRepository;
        this.usuarioService = usuarioService;
        this.emailService = emailService;
        this.resetPasswordUrl = resetPasswordUrl;
    }

    public void solicitarReset(String email) {
        usuarioService.buscarAtivoPorEmail(email)
                .ifPresent(usuario -> {
                    invalidarTokensAtivos(usuario);

                    String token = gerarToken();
                    PasswordResetToken resetToken = new PasswordResetToken();
                    resetToken.setUsuario(usuario);
                    resetToken.setTokenHash(hash(token));
                    resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES));

                    tokenRepository.save(resetToken);
                    emailService.enviarResetSenha(usuario.getEmail(), usuario.getNome(), montarResetLink(token));
                });
    }

    public void resetarSenha(String token, String novaSenha) {
        PasswordResetToken resetToken = tokenRepository.findByTokenHashAndUsedAtIsNull(hash(token))
                .orElseThrow(() -> new DomainException("Token de recuperação inválido ou expirado"));

        if (resetToken.isExpired() || resetToken.isUsed()) {
            throw new DomainException("Token de recuperação inválido ou expirado");
        }

        usuarioService.alterarSenha(resetToken.getUsuario(), novaSenha);
        resetToken.setUsedAt(LocalDateTime.now());
        tokenRepository.save(resetToken);
    }

    private void invalidarTokensAtivos(Usuario usuario) {
        tokenRepository.findByUsuarioAndUsedAtIsNull(usuario)
                .forEach(token -> token.setUsedAt(LocalDateTime.now()));
    }

    private String montarResetLink(String token) {
        return UriComponentsBuilder.fromUriString(resetPasswordUrl)
                .queryParam("token", token)
                .build()
                .toUriString();
    }

    private String gerarToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 indisponível", exception);
        }
    }
}
