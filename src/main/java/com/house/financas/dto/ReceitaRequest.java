package com.house.financas.dto;

import com.house.financas.model.enums.TipoReceita;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceitaRequest {

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 150, message = "Descrição deve ter no máximo 150 caracteres")
    private String descricao;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "Dia de recebimento é obrigatório")
    @Min(value = 1, message = "Dia de recebimento deve ser no mínimo 1")
    @Max(value = 31, message = "Dia de recebimento deve ser no máximo 31")
    private Integer diaRecebimento;

    @NotNull(message = "Mês é obrigatório")
    @Min(value = 1, message = "Mês deve ser no mínimo 1")
    @Max(value = 12, message = "Mês deve ser no máximo 12")
    private Integer mes;

    @NotNull(message = "Ano é obrigatório")
    @Min(value = 2000, message = "Ano deve ser no mínimo 2000")
    private Integer ano;

    @NotNull(message = "Tipo de receita é obrigatório")
    private TipoReceita tipoReceita;

    @NotBlank(message = "Origem é obrigatória")
    @Size(max = 150, message = "Origem deve ter no máximo 150 caracteres")
    private String origem;

    @NotNull(message = "Recorrente é obrigatório")
    private Boolean recorrente;
}
