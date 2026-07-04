package com.house.financas.controller;

import com.house.financas.dto.ReceitaRequest;
import com.house.financas.dto.ReceitaResponse;
import com.house.financas.model.Receita;
import com.house.financas.model.Usuario;
import com.house.financas.service.ReceitaService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/receitas")
@RequiredArgsConstructor
public class ReceitaController {

    private final ReceitaService receitaService;

    @GetMapping
    public ResponseEntity<List<ReceitaResponse>> listar(@AuthenticationPrincipal Usuario usuario) {
        List<ReceitaResponse> receitas = receitaService.listar(usuario)
                .stream()
                .map(ReceitaResponse::from)
                .toList();

        return ResponseEntity.ok(receitas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceitaResponse> buscar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        return ResponseEntity.ok(ReceitaResponse.from(receitaService.buscarPorId(id, usuario)));
    }

    @PostMapping
    public ResponseEntity<ReceitaResponse> cadastrar(
            @RequestBody @Valid ReceitaRequest request,
            @AuthenticationPrincipal Usuario usuario) {

        Receita receita = receitaService.cadastrar(request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(ReceitaResponse.from(receita));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReceitaResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ReceitaRequest request,
            @AuthenticationPrincipal Usuario usuario) {

        Receita receita = receitaService.atualizar(id, request, usuario);
        return ResponseEntity.ok(ReceitaResponse.from(receita));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        receitaService.deletar(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
