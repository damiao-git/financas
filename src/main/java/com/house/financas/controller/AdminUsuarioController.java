package com.house.financas.controller;

import com.house.financas.dto.AdminUsuarioRoleRequest;
import com.house.financas.dto.AdminUsuarioSenhaTemporariaRequest;
import com.house.financas.dto.UsuarioResponse;
import com.house.financas.model.Usuario;
import com.house.financas.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/usuarios")
@RequiredArgsConstructor
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        List<UsuarioResponse> usuarios = usuarioService.listarTodos()
                .stream()
                .map(UsuarioResponse::from)
                .toList();

        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(UsuarioResponse.from(usuarioService.buscarPorId(id)));
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<UsuarioResponse> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(UsuarioResponse.from(usuarioService.ativar(id)));
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<UsuarioResponse> desativar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario adminAutenticado) {

        return ResponseEntity.ok(UsuarioResponse.from(usuarioService.desativar(id, adminAutenticado)));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UsuarioResponse> alterarRole(
            @PathVariable Long id,
            @RequestBody @Valid AdminUsuarioRoleRequest request,
            @AuthenticationPrincipal Usuario adminAutenticado) {

        return ResponseEntity.ok(UsuarioResponse.from(
                usuarioService.alterarRole(id, request.getRole(), adminAutenticado)
        ));
    }

    @PatchMapping("/{id}/senha-temporaria")
    public ResponseEntity<UsuarioResponse> definirSenhaTemporaria(
            @PathVariable Long id,
            @RequestBody @Valid AdminUsuarioSenhaTemporariaRequest request,
            @AuthenticationPrincipal Usuario adminAutenticado) {

        return ResponseEntity.ok(UsuarioResponse.from(
                usuarioService.definirSenhaTemporaria(id, request.getSenhaTemporaria(), adminAutenticado)
        ));
    }
}
