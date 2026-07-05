package com.house.financas.service;

import com.house.financas.dto.FluxoCaixaMensalResponse;
import com.house.financas.dto.FluxoCaixaProjetadoMesResponse;
import com.house.financas.dto.FluxoCaixaProjecaoResponse;
import com.house.financas.model.Usuario;
import com.house.financas.model.enums.StatusContaMensal;
import com.house.financas.repository.AmortizacaoRepository;
import com.house.financas.repository.ContaMensalRepository;
import com.house.financas.repository.PagamentoRepository;
import com.house.financas.repository.ReceitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FluxoCaixaService {

    private final ReceitaRepository receitaRepository;
    private final ContaMensalRepository contaMensalRepository;
    private final PagamentoRepository pagamentoRepository;
    private final AmortizacaoRepository amortizacaoRepository;

    @Transactional(readOnly = true)
    public FluxoCaixaMensalResponse consultarMensal(Usuario usuario, Integer ano, Integer mes) {
        DadosFluxoMensal dados = calcularDadosMensais(usuario, ano, mes);

        return new FluxoCaixaMensalResponse(
                ano,
                mes,
                dados.totalReceitas(),
                dados.totalDespesasPrevistas(),
                dados.totalContasPagas(),
                dados.totalContasPendentes(),
                dados.totalAmortizacoes(),
                dados.saldoPrevisto(),
                dados.saldoRealizado()
        );
    }

    @Transactional(readOnly = true)
    public FluxoCaixaProjecaoResponse projetar(
            Usuario usuario,
            Integer anoInicial,
            Integer mesInicial,
            Integer quantidadeMeses,
            BigDecimal saldoInicial) {

        YearMonth competenciaInicial = YearMonth.of(anoInicial, mesInicial);
        BigDecimal caixaAcumulado = valorOuZero(saldoInicial);
        List<FluxoCaixaProjetadoMesResponse> meses = new ArrayList<>();

        for (int i = 0; i < quantidadeMeses; i++) {
            YearMonth competencia = competenciaInicial.plusMonths(i);
            DadosFluxoMensal dados = calcularDadosMensais(
                    usuario,
                    competencia.getYear(),
                    competencia.getMonthValue()
            );

            caixaAcumulado = caixaAcumulado.add(dados.saldoPrevisto());
            meses.add(new FluxoCaixaProjetadoMesResponse(
                    competencia.getYear(),
                    competencia.getMonthValue(),
                    dados.totalReceitas(),
                    dados.totalDespesasPrevistas(),
                    dados.totalContasPagas(),
                    dados.totalContasPendentes(),
                    dados.totalAmortizacoes(),
                    dados.saldoPrevisto(),
                    caixaAcumulado
            ));
        }

        return new FluxoCaixaProjecaoResponse(
                anoInicial,
                mesInicial,
                quantidadeMeses,
                valorOuZero(saldoInicial),
                caixaAcumulado,
                meses
        );
    }

    private DadosFluxoMensal calcularDadosMensais(Usuario usuario, Integer ano, Integer mes) {
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
        BigDecimal totalAmortizacoes = valorOuZero(
                amortizacaoRepository.somarAmortizacoesAtivasPorCompetencia(usuario.getId(), ano, mes)
        );

        BigDecimal saldoPrevisto = totalReceitas
                .subtract(totalDespesasPrevistas)
                .subtract(totalAmortizacoes);
        BigDecimal saldoRealizado = totalReceitas
                .subtract(totalContasPagas)
                .subtract(totalAmortizacoes);

        return new DadosFluxoMensal(
                totalReceitas,
                totalDespesasPrevistas,
                totalContasPagas,
                totalContasPendentes,
                totalAmortizacoes,
                saldoPrevisto,
                saldoRealizado
        );
    }

    private BigDecimal valorOuZero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    private record DadosFluxoMensal(
            BigDecimal totalReceitas,
            BigDecimal totalDespesasPrevistas,
            BigDecimal totalContasPagas,
            BigDecimal totalContasPendentes,
            BigDecimal totalAmortizacoes,
            BigDecimal saldoPrevisto,
            BigDecimal saldoRealizado
    ) {
    }
}
