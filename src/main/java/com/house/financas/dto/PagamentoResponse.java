package com.house.financas.dto;

import com.house.financas.model.Pagamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PagamentoResponse(
        Long id,
        Long contaMensalId,
        BigDecimal valorPago,
        LocalDate dataPagamento,
        String observacao,
        Boolean ativo,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {

    public static PagamentoResponse from(Pagamento pagamento) {
        return new PagamentoResponse(
                pagamento.getId(),
                pagamento.getContaMensal().getId(),
                pagamento.getValorPago(),
                pagamento.getDataPagamento(),
                pagamento.getObservacao(),
                pagamento.getAtivo(),
                pagamento.getDataCriacao(),
                pagamento.getDataAtualizacao()
        );
    }
}
