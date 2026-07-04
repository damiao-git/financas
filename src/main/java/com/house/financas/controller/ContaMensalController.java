package com.house.financas.controller;

import com.house.financas.dto.ContaMensalRequest;
import com.house.financas.dto.ContaMensalResponse;
import com.house.financas.model.ContaMensal;
import com.house.financas.model.Usuario;
import com.house.financas.service.ContaMensalService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contas-mensais")
@RequiredArgsConstructor
public class ContaMensalController {

    private final ContaMensalService contaMensalService;

    @GetMapping
    public ResponseEntity<List<ContaMensalResponse>> listar(
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer mes,
            @AuthenticationPrincipal Usuario usuario) {

        List<ContaMensalResponse> contas = contaMensalService.listar(usuario, ano, mes)
                .stream()
                .map(ContaMensalResponse::from)
                .toList();

        return ResponseEntity.ok(contas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaMensalResponse> buscar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        return ResponseEntity.ok(ContaMensalResponse.from(contaMensalService.buscarPorId(id, usuario)));
    }

    @PostMapping
    public ResponseEntity<ContaMensalResponse> cadastrar(
            @RequestBody @Valid ContaMensalRequest request,
            @AuthenticationPrincipal Usuario usuario) {

        ContaMensal contaMensal = contaMensalService.cadastrar(request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(ContaMensalResponse.from(contaMensal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaMensalResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ContaMensalRequest request,
            @AuthenticationPrincipal Usuario usuario) {

        ContaMensal contaMensal = contaMensalService.atualizar(id, request, usuario);
        return ResponseEntity.ok(ContaMensalResponse.from(contaMensal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        contaMensalService.cancelar(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
