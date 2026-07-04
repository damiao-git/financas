package com.house.financas.dto;

import com.house.financas.model.enums.TipoDespesa;
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
public class DespesaRequest {

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 150, message = "Descricao deve ter no maximo 150 caracteres")
    private String descricao;

    @NotNull(message = "Valor e obrigatorio")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "Dia de vencimento e obrigatorio")
    @Min(value = 1, message = "Dia de vencimento deve ser no minimo 1")
    @Max(value = 31, message = "Dia de vencimento deve ser no maximo 31")
    private Integer diaVencimento;

    @NotNull(message = "Tipo de despesa e obrigatorio")
    private TipoDespesa tipoDespesa;

    @NotNull(message = "Recorrente e obrigatorio")
    private Boolean recorrente;

    @NotNull(message = "Categoria e obrigatoria")
    private Long categoriaId;
}
