package com.house.financas.controller;

import com.house.financas.dto.AmortizacaoRequest;
import com.house.financas.dto.AmortizacaoResponse;
import com.house.financas.model.Amortizacao;
import com.house.financas.model.Usuario;
import com.house.financas.service.AmortizacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AmortizacaoController {

    private final AmortizacaoService amortizacaoService;

    @GetMapping("/dividas/{dividaId}/amortizacoes")
    public ResponseEntity<List<AmortizacaoResponse>> listarPorDivida(
            @PathVariable Long dividaId,
            @AuthenticationPrincipal Usuario usuario) {

        List<AmortizacaoResponse> amortizacoes = amortizacaoService.listarPorDivida(dividaId, usuario)
                .stream()
                .map(AmortizacaoResponse::from)
                .toList();

        return ResponseEntity.ok(amortizacoes);
    }

    @GetMapping("/amortizacoes")
    public ResponseEntity<List<AmortizacaoResponse>> listarPorCompetencia(
            @RequestParam Integer ano,
            @RequestParam Integer mes,
            @AuthenticationPrincipal Usuario usuario) {

        List<AmortizacaoResponse> amortizacoes = amortizacaoService.listarPorCompetencia(usuario, ano, mes)
                .stream()
                .map(AmortizacaoResponse::from)
                .toList();

        return ResponseEntity.ok(amortizacoes);
    }

    @PostMapping("/dividas/{dividaId}/amortizacoes")
    public ResponseEntity<AmortizacaoResponse> cadastrar(
            @PathVariable Long dividaId,
            @RequestBody @Valid AmortizacaoRequest request,
            @AuthenticationPrincipal Usuario usuario) {

        Amortizacao amortizacao = amortizacaoService.cadastrar(dividaId, request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(AmortizacaoResponse.from(amortizacao));
    }

    @PutMapping("/amortizacoes/{id}")
    public ResponseEntity<AmortizacaoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid AmortizacaoRequest request,
            @AuthenticationPrincipal Usuario usuario) {

        Amortizacao amortizacao = amortizacaoService.atualizar(id, request, usuario);
        return ResponseEntity.ok(AmortizacaoResponse.from(amortizacao));
    }

    @DeleteMapping("/amortizacoes/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        amortizacaoService.deletar(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
