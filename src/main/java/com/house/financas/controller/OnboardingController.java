package com.house.financas.controller;

import com.house.financas.dto.OnboardingStatusResponse;
import com.house.financas.model.Usuario;
import com.house.financas.service.OnboardingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;

    @GetMapping("/status")
    public ResponseEntity<OnboardingStatusResponse> status(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(onboardingService.status(usuario));
    }

    @PatchMapping("/concluir")
    public ResponseEntity<OnboardingStatusResponse> concluir(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(onboardingService.concluir(usuario));
    }
}
