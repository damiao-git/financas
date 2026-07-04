package com.house.financas.dto;

import java.math.BigDecimal;

public record FluxoCaixaMensalResponse(
        Integer ano,
        Integer mes,
        BigDecimal totalReceitas,
        BigDecimal totalDespesasPrevistas,
        BigDecimal totalContasPagas,
        BigDecimal totalContasPendentes,
        BigDecimal saldoPrevisto,
        BigDecimal saldoRealizado
) {
}
