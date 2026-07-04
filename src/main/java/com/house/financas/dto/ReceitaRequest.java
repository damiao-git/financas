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

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 150, message = "Descricao deve ter no maximo 150 caracteres")
    private String descricao;

    @NotNull(message = "Valor e obrigatorio")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "Dia de recebimento e obrigatorio")
    @Min(value = 1, message = "Dia de recebimento deve ser no minimo 1")
    @Max(value = 31, message = "Dia de recebimento deve ser no maximo 31")
    private Integer diaRecebimento;

    @NotNull(message = "Mes e obrigatorio")
    @Min(value = 1, message = "Mes deve ser no minimo 1")
    @Max(value = 12, message = "Mes deve ser no maximo 12")
    private Integer mes;

    @NotNull(message = "Ano e obrigatorio")
    @Min(value = 2000, message = "Ano deve ser no minimo 2000")
    private Integer ano;

    @NotNull(message = "Tipo de receita e obrigatorio")
    private TipoReceita tipoReceita;

    @NotBlank(message = "Origem e obrigatoria")
    @Size(max = 150, message = "Origem deve ter no maximo 150 caracteres")
    private String origem;

    @NotNull(message = "Recorrente e obrigatorio")
    private Boolean recorrente;
}
