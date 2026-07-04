package com.house.financas.dto;

import com.house.financas.model.Receita;
import com.house.financas.model.enums.TipoReceita;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReceitaResponse(
        Long id,
        String descricao,
        BigDecimal valor,
        Integer diaRecebimento,
        Integer mes,
        Integer ano,
        TipoReceita tipoReceita,
        String origem,
        Boolean recorrente,
        Boolean ativo,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {

    public static ReceitaResponse from(Receita receita) {
        return new ReceitaResponse(
                receita.getId(),
                receita.getDescricao(),
                receita.getValor(),
                receita.getDiaRecebimento(),
                receita.getMes(),
                receita.getAno(),
                receita.getTipoReceita(),
                receita.getOrigem(),
                receita.getRecorrente(),
                receita.getAtivo(),
                receita.getDataCriacao(),
                receita.getDataAtualizacao()
        );
    }
}
