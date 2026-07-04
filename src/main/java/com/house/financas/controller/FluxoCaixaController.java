package com.house.financas.controller;

import com.house.financas.dto.FluxoCaixaMensalResponse;
import com.house.financas.model.Usuario;
import com.house.financas.service.FluxoCaixaService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/fluxo-caixa")
@RequiredArgsConstructor
public class FluxoCaixaController {

    private final FluxoCaixaService fluxoCaixaService;

    @GetMapping
    public ResponseEntity<FluxoCaixaMensalResponse> consultarMensal(
            @RequestParam @NotNull @Min(2000) Integer ano,
            @RequestParam @NotNull @Min(1) @Max(12) Integer mes,
            @AuthenticationPrincipal Usuario usuario) {

        return ResponseEntity.ok(fluxoCaixaService.consultarMensal(usuario, ano, mes));
    }
}
