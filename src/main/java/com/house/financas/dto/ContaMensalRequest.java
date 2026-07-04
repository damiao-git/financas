package com.house.financas.dto;

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
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaMensalRequest {

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 150, message = "Descricao deve ter no maximo 150 caracteres")
    private String descricao;

    @NotNull(message = "Valor previsto e obrigatorio")
    @DecimalMin(value = "0.01", message = "Valor previsto deve ser maior que zero")
    private BigDecimal valorPrevisto;

    @NotNull(message = "Data de vencimento e obrigatoria")
    private LocalDate dataVencimento;

    @NotNull(message = "Mes e obrigatorio")
    @Min(value = 1, message = "Mes deve ser no minimo 1")
    @Max(value = 12, message = "Mes deve ser no maximo 12")
    private Integer mes;

    @NotNull(message = "Ano e obrigatorio")
    @Min(value = 2000, message = "Ano deve ser no minimo 2000")
    private Integer ano;

    private Long categoriaId;

    private Long despesaId;

    @Size(max = 255, message = "Observacao deve ter no maximo 255 caracteres")
    private String observacao;
}
