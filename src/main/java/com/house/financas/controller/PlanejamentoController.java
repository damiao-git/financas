package com.house.financas.controller;

import com.house.financas.dto.GeracaoRecorrenciasResponse;
import com.house.financas.model.Usuario;
import com.house.financas.service.PlanejamentoService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/planejamento")
@RequiredArgsConstructor
public class PlanejamentoController {

    private final PlanejamentoService planejamentoService;

    @PostMapping("/gerar-recorrencias")
    public ResponseEntity<GeracaoRecorrenciasResponse> gerarRecorrencias(
            @RequestParam @NotNull @Min(2000) Integer anoInicial,
            @RequestParam @NotNull @Min(1) @Max(12) Integer mesInicial,
            @RequestParam @NotNull @Min(1) @Max(120) Integer quantidadeMeses,
            @AuthenticationPrincipal Usuario usuario) {

        return ResponseEntity.ok(planejamentoService.gerarRecorrencias(
                usuario,
                anoInicial,
                mesInicial,
                quantidadeMeses
        ));
    }
}
