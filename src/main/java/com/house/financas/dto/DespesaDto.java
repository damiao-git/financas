package com.house.financas.dto;

import lombok.Data;

@Data
public class DespesaDto {
    private String descricao;
    private Double valor;
    private Long diaVencimento;
    private Long categoriaId;
}
