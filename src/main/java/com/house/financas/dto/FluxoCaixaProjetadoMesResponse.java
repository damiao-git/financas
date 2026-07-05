package com.house.financas.dto;

import java.math.BigDecimal;

public record FluxoCaixaProjetadoMesResponse(
        Integer ano,
        Integer mes,
        BigDecimal totalReceitas,
        BigDecimal totalDespesasPrevistas,
        BigDecimal totalContasPagas,
        BigDecimal totalContasPendentes,
        BigDecimal totalAmortizacoes,
        BigDecimal saldoDoMes,
        BigDecimal caixaAcumulado
) {
}
