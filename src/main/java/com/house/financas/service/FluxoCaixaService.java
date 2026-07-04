package com.house.financas.service;

import com.house.financas.dto.FluxoCaixaMensalResponse;
import com.house.financas.model.Usuario;
import com.house.financas.model.enums.StatusContaMensal;
import com.house.financas.repository.ContaMensalRepository;
import com.house.financas.repository.PagamentoRepository;
import com.house.financas.repository.ReceitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FluxoCaixaService {

    private final ReceitaRepository receitaRepository;
    private final ContaMensalRepository contaMensalRepository;
    private final PagamentoRepository pagamentoRepository;

    @Transactional(readOnly = true)
    public FluxoCaixaMensalResponse consultarMensal(Usuario usuario, Integer ano, Integer mes) {
        BigDecimal totalReceitas = valorOuZero(
                receitaRepository.somarReceitasAtivasPorCompetencia(usuario.getId(), ano, mes)
        );
        BigDecimal totalDespesasPrevistas = valorOuZero(
                contaMensalRepository.somarValorPrevistoPorMes(usuario.getId(), ano, mes)
        );
        BigDecimal totalContasPagas = valorOuZero(
                pagamentoRepository.somarPagamentosAtivosPorMes(usuario.getId(), ano, mes)
        );
        BigDecimal totalContasPendentes = valorOuZero(
                contaMensalRepository.somarValorPrevistoPorStatus(
                        usuario.getId(),
                        ano,
                        mes,
                        StatusContaMensal.PENDENTE
                )
        );

        BigDecimal saldoPrevisto = totalReceitas.subtract(totalDespesasPrevistas);
        BigDecimal saldoRealizado = totalReceitas.subtract(totalContasPagas);

        return new FluxoCaixaMensalResponse(
                ano,
                mes,
                totalReceitas,
                totalDespesasPrevistas,
                totalContasPagas,
                totalContasPendentes,
                saldoPrevisto,
                saldoRealizado
        );
    }

    private BigDecimal valorOuZero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }
}
