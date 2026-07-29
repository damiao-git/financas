package com.house.financas.controller;

import com.house.financas.dto.UsuarioResponse;
import com.house.financas.dto.UsuarioUpdateRequest;
import com.house.financas.model.Usuario;
import com.house.financas.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> buscarPerfil(@AuthenticationPrincipal Usuario usuarioAutenticado) {
        Usuario usuario = usuarioService.sincronizarRoleConfigurada(usuarioAutenticado);
        return ResponseEntity.ok(UsuarioResponse.from(usuario));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> atualizarPerfil(
            @AuthenticationPrincipal Usuario usuarioAutenticado,
            @RequestBody @Valid UsuarioUpdateRequest request) {

        Usuario usuario = usuarioService.atualizar(usuarioAutenticado.getId(), request);
        return ResponseEntity.ok(UsuarioResponse.from(usuario));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> desativarPerfil(@AuthenticationPrincipal Usuario usuarioAutenticado) {
        usuarioService.deletar(usuarioAutenticado.getId());
        return ResponseEntity.noContent().build();
    }
}
