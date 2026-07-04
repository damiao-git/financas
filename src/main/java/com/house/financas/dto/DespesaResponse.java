package com.house.financas.dto;

import com.house.financas.model.Despesa;
import com.house.financas.model.enums.TipoDespesa;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DespesaResponse(
        Long id,
        String descricao,
        BigDecimal valor,
        Integer diaVencimento,
        TipoDespesa tipoDespesa,
        Boolean recorrente,
        Boolean ativo,
        CategoriaResumoResponse categoria,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {

    public static DespesaResponse from(Despesa despesa) {
        return new DespesaResponse(
                despesa.getId(),
                despesa.getDescricao(),
                despesa.getValor(),
                despesa.getDiaVencimento(),
                despesa.getTipoDespesa(),
                despesa.getRecorrente(),
                despesa.getAtivo(),
                CategoriaResumoResponse.from(despesa.getCategoria()),
                despesa.getDataCriacao(),
                despesa.getDataAtualizacao()
        );
    }
}
