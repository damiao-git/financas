package com.house.financas.controller;

import com.house.financas.dto.DespesaRequest;
import com.house.financas.dto.DespesaResponse;
import com.house.financas.model.Despesa;
import com.house.financas.model.Usuario;
import com.house.financas.service.DespesaService;
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
@RequestMapping("/despesas")
@RequiredArgsConstructor
public class DespesaController {

    private final DespesaService despesaService;

    @GetMapping
    public ResponseEntity<List<DespesaResponse>> listar(@AuthenticationPrincipal Usuario usuario) {
        List<DespesaResponse> despesas = despesaService.listar(usuario)
                .stream()
                .map(DespesaResponse::from)
                .toList();

        return ResponseEntity.ok(despesas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DespesaResponse> buscar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        return ResponseEntity.ok(DespesaResponse.from(despesaService.buscarPorId(id, usuario)));
    }

    @PostMapping
    public ResponseEntity<DespesaResponse> cadastrar(
            @RequestBody @Valid DespesaRequest request,
            @AuthenticationPrincipal Usuario usuario) {

        Despesa despesa = despesaService.cadastrar(request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(DespesaResponse.from(despesa));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DespesaResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DespesaRequest request,
            @AuthenticationPrincipal Usuario usuario) {

        Despesa despesa = despesaService.atualizar(id, request, usuario);
        return ResponseEntity.ok(DespesaResponse.from(despesa));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        despesaService.deletar(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
