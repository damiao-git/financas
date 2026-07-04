package com.house.financas.dto;

import jakarta.validation.constraints.DecimalMin;
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
public class PagamentoRequest {

    @NotNull(message = "Valor pago e obrigatorio")
    @DecimalMin(value = "0.01", message = "Valor pago deve ser maior que zero")
    private BigDecimal valorPago;

    @NotNull(message = "Data de pagamento e obrigatoria")
    private LocalDate dataPagamento;

    @Size(max = 255, message = "Observacao deve ter no maximo 255 caracteres")
    private String observacao;
}
