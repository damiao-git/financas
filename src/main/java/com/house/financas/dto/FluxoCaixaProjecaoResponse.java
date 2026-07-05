package com.house.financas.dto;

import java.math.BigDecimal;
import java.util.List;

public record FluxoCaixaProjecaoResponse(
        Integer anoInicial,
        Integer mesInicial,
        Integer quantidadeMeses,
        BigDecimal saldoInicial,
        BigDecimal saldoFinal,
        List<FluxoCaixaProjetadoMesResponse> meses
) {
}
