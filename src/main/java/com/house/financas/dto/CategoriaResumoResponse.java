package com.house.financas.dto;

import com.house.financas.model.Categoria;

public record CategoriaResumoResponse(
        Long id,
        String nome
) {

    public static CategoriaResumoResponse from(Categoria categoria) {
        return new CategoriaResumoResponse(
                categoria.getId(),
                categoria.getNome()
        );
    }
}
