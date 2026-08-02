package com.house.financas.dto;

import com.house.financas.model.Usuario;
import com.house.financas.model.enums.RoleUsuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        RoleUsuario role,
        Boolean ativo,
        Boolean trocarSenhaNoProximoLogin,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole() == null ? RoleUsuario.USER : usuario.getRole(),
                usuario.getAtivo(),
                Boolean.TRUE.equals(usuario.getTrocarSenhaNoProximoLogin()),
                usuario.getDataCriacao(),
                usuario.getDataAtualizacao()
        );
    }
}
