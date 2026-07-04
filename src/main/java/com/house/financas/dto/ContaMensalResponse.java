package com.house.financas.dto;

import com.house.financas.model.ContaMensal;
import com.house.financas.model.enums.StatusContaMensal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ContaMensalResponse(
        Long id,
        String descricao,
        BigDecimal valorPrevisto,
        LocalDate dataVencimento,
        Integer mes,
        Integer ano,
        StatusContaMensal status,
        String observacao,
        Boolean ativo,
        CategoriaResumoResponse categoria,
        Long despesaId,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {

    public static ContaMensalResponse from(ContaMensal contaMensal) {
        CategoriaResumoResponse categoria = contaMensal.getCategoria() == null
                ? null
                : CategoriaResumoResponse.from(contaMensal.getCategoria());

        Long despesaId = contaMensal.getDespesa() == null
                ? null
                : contaMensal.getDespesa().getId();

        return new ContaMensalResponse(
                contaMensal.getId(),
                contaMensal.getDescricao(),
                contaMensal.getValorPrevisto(),
                contaMensal.getDataVencimento(),
                contaMensal.getMes(),
                contaMensal.getAno(),
                contaMensal.getStatus(),
                contaMensal.getObservacao(),
                contaMensal.getAtivo(),
                categoria,
                despesaId,
                contaMensal.getDataCriacao(),
                contaMensal.getDataAtualizacao()
        );
    }
}
