package com.house.financas.dto;

import com.house.financas.model.Divida;
import com.house.financas.model.enums.StatusDivida;
import com.house.financas.model.enums.TipoDivida;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public record DividaResponse(
        Long id,
        String descricao,
        String instituicao,
        TipoDivida tipoDivida,
        BigDecimal saldoInicial,
        BigDecimal saldoAtual,
        BigDecimal valorParcela,
        Integer diaVencimento,
        Integer quantidadeParcelas,
        Integer parcelasPagas,
        BigDecimal taxaJurosMensal,
        StatusDivida status,
        String observacao,
        Boolean ativo,
        BigDecimal percentualQuitado,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {

    public static DividaResponse from(Divida divida) {
        return new DividaResponse(
                divida.getId(),
                divida.getDescricao(),
                divida.getInstituicao(),
                divida.getTipoDivida(),
                divida.getSaldoInicial(),
                divida.getSaldoAtual(),
                divida.getValorParcela(),
                divida.getDiaVencimento(),
                divida.getQuantidadeParcelas(),
                divida.getParcelasPagas(),
                divida.getTaxaJurosMensal(),
                divida.getStatus(),
                divida.getObservacao(),
                divida.getAtivo(),
                calcularPercentualQuitado(divida),
                divida.getDataCriacao(),
                divida.getDataAtualizacao()
        );
    }

    private static BigDecimal calcularPercentualQuitado(Divida divida) {
        BigDecimal valorQuitado = divida.getSaldoInicial().subtract(divida.getSaldoAtual());

        if (valorQuitado.signum() <= 0) {
            return BigDecimal.ZERO;
        }

        return valorQuitado
                .multiply(BigDecimal.valueOf(100))
                .divide(divida.getSaldoInicial(), 2, RoundingMode.HALF_UP);
    }
}
