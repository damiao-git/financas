package com.house.financas.controller;

import com.house.financas.dto.DividaRequest;
import com.house.financas.dto.DividaResponse;
import com.house.financas.model.Divida;
import com.house.financas.model.Usuario;
import com.house.financas.model.enums.StatusDivida;
import com.house.financas.service.DividaService;
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
@RequestMapping("/dividas")
@RequiredArgsConstructor
public class DividaController {

    private final DividaService dividaService;

    @GetMapping
    public ResponseEntity<List<DividaResponse>> listar(
            @RequestParam(required = false) StatusDivida status,
            @AuthenticationPrincipal Usuario usuario) {

        List<DividaResponse> dividas = dividaService.listar(usuario, status)
                .stream()
                .map(DividaResponse::from)
                .toList();

        return ResponseEntity.ok(dividas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DividaResponse> buscar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        return ResponseEntity.ok(DividaResponse.from(dividaService.buscarPorId(id, usuario)));
    }

    @PostMapping
    public ResponseEntity<DividaResponse> cadastrar(
            @RequestBody @Valid DividaRequest request,
            @AuthenticationPrincipal Usuario usuario) {

        Divida divida = dividaService.cadastrar(request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(DividaResponse.from(divida));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DividaResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DividaRequest request,
            @AuthenticationPrincipal Usuario usuario) {

        Divida divida = dividaService.atualizar(id, request, usuario);
        return ResponseEntity.ok(DividaResponse.from(divida));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        dividaService.cancelar(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
