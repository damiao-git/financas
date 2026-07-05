package com.house.financas.dto;

public record GeracaoRecorrenciasResponse(
        Integer anoInicial,
        Integer mesInicial,
        Integer quantidadeMeses,
        Integer receitasGeradas,
        Integer contasGeradas,
        Integer parcelasDividaGeradas
) {
}
