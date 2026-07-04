package com.house.financas.dto;

import com.house.financas.model.Categoria;

import java.time.LocalDateTime;

public record CategoriaResponse(
        Long id,
        String nome,
        Boolean ativo,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {

    public static CategoriaResponse from(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getAtivo(),
                categoria.getDataCriacao(),
                categoria.getDataAtualizacao()
        );
    }
}
