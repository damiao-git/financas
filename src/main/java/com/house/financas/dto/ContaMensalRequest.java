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

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 150, message = "Descrição deve ter no máximo 150 caracteres")
    private String descricao;

    @NotNull(message = "Valor previsto é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor previsto deve ser maior que zero")
    private BigDecimal valorPrevisto;

    @NotNull(message = "Data de vencimento é obrigatória")
    private LocalDate dataVencimento;

    @NotNull(message = "Mês é obrigatório")
    @Min(value = 1, message = "Mês deve ser no mínimo 1")
    @Max(value = 12, message = "Mês deve ser no máximo 12")
    private Integer mes;

    @NotNull(message = "Ano é obrigatório")
    @Min(value = 2000, message = "Ano deve ser no mínimo 2000")
    private Integer ano;

    private Long categoriaId;

    private Long despesaId;

    @Size(max = 255, message = "Observação deve ter no máximo 255 caracteres")
    private String observacao;
}
