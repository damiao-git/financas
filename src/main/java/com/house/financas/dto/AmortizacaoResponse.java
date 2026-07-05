package com.house.financas.dto;

import com.house.financas.model.Amortizacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AmortizacaoResponse(
        Long id,
        Long dividaId,
        String dividaDescricao,
        BigDecimal valor,
        LocalDate dataAmortizacao,
        Integer mes,
        Integer ano,
        String observacao,
        Boolean ativo,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {

    public static AmortizacaoResponse from(Amortizacao amortizacao) {
        return new AmortizacaoResponse(
                amortizacao.getId(),
                amortizacao.getDivida().getId(),
                amortizacao.getDivida().getDescricao(),
                amortizacao.getValor(),
                amortizacao.getDataAmortizacao(),
                amortizacao.getMes(),
                amortizacao.getAno(),
                amortizacao.getObservacao(),
                amortizacao.getAtivo(),
                amortizacao.getDataCriacao(),
                amortizacao.getDataAtualizacao()
        );
    }
}
