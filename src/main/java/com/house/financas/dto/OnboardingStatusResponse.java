package com.house.financas.dto;

public record OnboardingStatusResponse(
        boolean concluido,
        boolean possuiReceita,
        boolean possuiContaOuDespesa,
        boolean possuiDivida,
        String proximoPasso
) {
}
