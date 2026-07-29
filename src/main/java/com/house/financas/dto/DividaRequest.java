package com.house.financas.dto;

import com.house.financas.model.enums.TipoDivida;
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
public class DividaRequest {

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 150, message = "Descrição deve ter no máximo 150 caracteres")
    private String descricao;

    @NotBlank(message = "Instituição é obrigatória")
    @Size(max = 150, message = "Instituição deve ter no máximo 150 caracteres")
    private String instituicao;

    @NotNull(message = "Tipo da dívida é obrigatório")
    private TipoDivida tipoDivida;

    @NotNull(message = "Saldo inicial é obrigatório")
    @DecimalMin(value = "0.01", message = "Saldo inicial deve ser maior que zero")
    private BigDecimal saldoInicial;

    @NotNull(message = "Saldo atual é obrigatório")
    @DecimalMin(value = "0.00", message = "Saldo atual não pode ser negativo")
    private BigDecimal saldoAtual;

    @NotNull(message = "Valor da parcela é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor da parcela deve ser maior que zero")
    private BigDecimal valorParcela;

    @NotNull(message = "Dia de vencimento é obrigatório")
    @Min(value = 1, message = "Dia de vencimento deve ser no mínimo 1")
    @Max(value = 31, message = "Dia de vencimento deve ser no máximo 31")
    private Integer diaVencimento;

    @NotNull(message = "Quantidade de parcelas é obrigatória")
    @Min(value = 1, message = "Quantidade de parcelas deve ser no mínimo 1")
    private Integer quantidadeParcelas;

    @NotNull(message = "Parcelas pagas são obrigatórias")
    @Min(value = 0, message = "Parcelas pagas não podem ser negativas")
    private Integer parcelasPagas;

    @DecimalMin(value = "0.00", message = "Taxa de juros mensal não pode ser negativa")
    private BigDecimal taxaJurosMensal;

    @Size(max = 255, message = "Observação deve ter no máximo 255 caracteres")
    private String observacao;
}
