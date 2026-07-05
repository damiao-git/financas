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

    @NotBlank(message = "Descricao e obrigatoria")
    @Size(max = 150, message = "Descricao deve ter no maximo 150 caracteres")
    private String descricao;

    @NotBlank(message = "Instituicao e obrigatoria")
    @Size(max = 150, message = "Instituicao deve ter no maximo 150 caracteres")
    private String instituicao;

    @NotNull(message = "Tipo da divida e obrigatorio")
    private TipoDivida tipoDivida;

    @NotNull(message = "Saldo inicial e obrigatorio")
    @DecimalMin(value = "0.01", message = "Saldo inicial deve ser maior que zero")
    private BigDecimal saldoInicial;

    @NotNull(message = "Saldo atual e obrigatorio")
    @DecimalMin(value = "0.00", message = "Saldo atual nao pode ser negativo")
    private BigDecimal saldoAtual;

    @NotNull(message = "Valor da parcela e obrigatorio")
    @DecimalMin(value = "0.01", message = "Valor da parcela deve ser maior que zero")
    private BigDecimal valorParcela;

    @NotNull(message = "Dia de vencimento e obrigatorio")
    @Min(value = 1, message = "Dia de vencimento deve ser no minimo 1")
    @Max(value = 31, message = "Dia de vencimento deve ser no maximo 31")
    private Integer diaVencimento;

    @NotNull(message = "Quantidade de parcelas e obrigatoria")
    @Min(value = 1, message = "Quantidade de parcelas deve ser no minimo 1")
    private Integer quantidadeParcelas;

    @NotNull(message = "Parcelas pagas e obrigatorio")
    @Min(value = 0, message = "Parcelas pagas nao pode ser negativo")
    private Integer parcelasPagas;

    @DecimalMin(value = "0.00", message = "Taxa de juros mensal nao pode ser negativa")
    private BigDecimal taxaJurosMensal;

    @Size(max = 255, message = "Observacao deve ter no maximo 255 caracteres")
    private String observacao;
}
