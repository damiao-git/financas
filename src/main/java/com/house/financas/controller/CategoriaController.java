package com.house.financas.controller;

import com.house.financas.dto.CategoriaRequest;
import com.house.financas.dto.CategoriaResponse;
import com.house.financas.model.Categoria;
import com.house.financas.model.Usuario;
import com.house.financas.service.CategoriaService;
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
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listar(@AuthenticationPrincipal Usuario usuario) {
        List<CategoriaResponse> categorias = categoriaService.listar(usuario)
                .stream()
                .map(CategoriaResponse::from)
                .toList();

        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> buscar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        return ResponseEntity.ok(CategoriaResponse.from(categoriaService.buscarPorId(id, usuario)));
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> cadastrar(
            @RequestBody @Valid CategoriaRequest request,
            @AuthenticationPrincipal Usuario usuario) {

        Categoria categoria = categoriaService.cadastrar(request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoriaResponse.from(categoria));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid CategoriaRequest request,
            @AuthenticationPrincipal Usuario usuario) {

        Categoria categoria = categoriaService.atualizar(id, request, usuario);
        return ResponseEntity.ok(CategoriaResponse.from(categoria));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        categoriaService.deletar(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
