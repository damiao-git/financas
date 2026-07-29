package com.house.financas.controller;

import com.house.financas.dto.GoogleCalendarAuthUrlResponse;
import com.house.financas.dto.GoogleCalendarStatusResponse;
import com.house.financas.model.Usuario;
import com.house.financas.service.GoogleCalendarIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/integracoes/google-calendar")
@RequiredArgsConstructor
public class GoogleCalendarIntegrationController {

    private final GoogleCalendarIntegrationService googleCalendarIntegrationService;

    @GetMapping("/status")
    public ResponseEntity<GoogleCalendarStatusResponse> status(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(googleCalendarIntegrationService.status(usuario));
    }

    @PostMapping("/autorizar")
    public ResponseEntity<GoogleCalendarAuthUrlResponse> autorizar(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(googleCalendarIntegrationService.iniciarAutorizacao(usuario));
    }

    @GetMapping("/callback")
    public ResponseEntity<Void> callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state
    ) {
        URI redirectUri = googleCalendarIntegrationService.concluirAutorizacao(code, state);
        return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> desconectar(@AuthenticationPrincipal Usuario usuario) {
        googleCalendarIntegrationService.desconectar(usuario);
        return ResponseEntity.noContent().build();
    }
}
