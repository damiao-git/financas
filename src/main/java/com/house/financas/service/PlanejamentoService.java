package com.house.financas.service;

import com.house.financas.dto.GeracaoRecorrenciasResponse;
import com.house.financas.model.ContaMensal;
import com.house.financas.model.Despesa;
import com.house.financas.model.Divida;
import com.house.financas.model.Receita;
import com.house.financas.model.Usuario;
import com.house.financas.model.enums.StatusContaMensal;
import com.house.financas.model.enums.StatusDivida;
import com.house.financas.repository.ContaMensalRepository;
import com.house.financas.repository.DespesaRepository;
import com.house.financas.repository.DividaRepository;
import com.house.financas.repository.ReceitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanejamentoService {

    private final ReceitaRepository receitaRepository;
    private final DespesaRepository despesaRepository;
    private final DividaRepository dividaRepository;
    private final ContaMensalRepository contaMensalRepository;
    private final GoogleCalendarIntegrationService googleCalendarIntegrationService;

    public GeracaoRecorrenciasResponse gerarRecorrencias(
            Usuario usuario,
            Integer anoInicial,
            Integer mesInicial,
            Integer quantidadeMeses) {

        YearMonth competenciaInicial = YearMonth.of(anoInicial, mesInicial);
        int receitasGeradas = gerarReceitas(usuario, competenciaInicial, quantidadeMeses);
        int contasGeradas = gerarContasDeDespesas(usuario, competenciaInicial, quantidadeMeses);
        int parcelasDividaGeradas = gerarParcelasDeDividas(usuario, competenciaInicial, quantidadeMeses);

        return new GeracaoRecorrenciasResponse(
                anoInicial,
                mesInicial,
                quantidadeMeses,
                receitasGeradas,
                contasGeradas,
                parcelasDividaGeradas
        );
    }

    private int gerarReceitas(Usuario usuario, YearMonth competenciaInicial, Integer quantidadeMeses) {
        List<Receita> receitasRecorrentes = receitaRepository.findByUsuarioIdAndRecorrenteTrueAndAtivoTrue(
                usuario.getId()
        );
        int geradas = 0;

        for (Receita modelo : receitasRecorrentes) {
            for (int i = 0; i < quantidadeMeses; i++) {
                YearMonth competencia = competenciaInicial.plusMonths(i);

                boolean jaExiste = receitaRepository
                        .existsByUsuarioIdAndAnoAndMesAndDescricaoIgnoreCaseAndOrigemIgnoreCaseAndTipoReceitaAndAtivoTrue(
                                usuario.getId(),
                                competencia.getYear(),
                                competencia.getMonthValue(),
                                modelo.getDescricao(),
                                modelo.getOrigem(),
                                modelo.getTipoReceita()
                        );

                if (jaExiste) {
                    continue;
                }

                Receita receita = new Receita();
                receita.setDescricao(modelo.getDescricao());
                receita.setValor(modelo.getValor());
                receita.setDiaRecebimento(modelo.getDiaRecebimento());
                receita.setMes(competencia.getMonthValue());
                receita.setAno(competencia.getYear());
                receita.setTipoReceita(modelo.getTipoReceita());
                receita.setOrigem(modelo.getOrigem());
                receita.setRecorrente(false);
                receita.setAtivo(true);
                receita.setUsuario(usuario);
                receitaRepository.save(receita);
                geradas++;
            }
        }

        return geradas;
    }

    private int gerarContasDeDespesas(Usuario usuario, YearMonth competenciaInicial, Integer quantidadeMeses) {
        List<Despesa> despesasRecorrentes = despesaRepository.findByUsuarioIdAndRecorrenteTrueAndAtivoTrue(
                usuario.getId()
        );
        int geradas = 0;

        for (Despesa despesa : despesasRecorrentes) {
            for (int i = 0; i < quantidadeMeses; i++) {
                YearMonth competencia = competenciaInicial.plusMonths(i);

                boolean jaExiste = contaMensalRepository.existsByUsuarioIdAndDespesaIdAndAnoAndMesAndAtivoTrue(
                        usuario.getId(),
                        despesa.getId(),
                        competencia.getYear(),
                        competencia.getMonthValue()
                );

                if (jaExiste) {
                    continue;
                }

                ContaMensal conta = new ContaMensal();
                conta.setDescricao(despesa.getDescricao());
                conta.setValorPrevisto(despesa.getValor());
                conta.setDataVencimento(dataDaCompetencia(competencia, despesa.getDiaVencimento()));
                conta.setMes(competencia.getMonthValue());
                conta.setAno(competencia.getYear());
                conta.setStatus(StatusContaMensal.PENDENTE);
                conta.setObservacao("Gerada automaticamente por despesa recorrente");
                conta.setAtivo(true);
                conta.setCategoria(despesa.getCategoria());
                conta.setDespesa(despesa);
                conta.setUsuario(usuario);
                ContaMensal contaSalva = contaMensalRepository.save(conta);
                googleCalendarIntegrationService.sincronizarConta(contaSalva);
                geradas++;
            }
        }

        return geradas;
    }

    private int gerarParcelasDeDividas(Usuario usuario, YearMonth competenciaInicial, Integer quantidadeMeses) {
        List<Divida> dividasAtivas = dividaRepository.findByUsuarioIdAndStatusAndAtivoTrueOrderByDescricaoAsc(
                usuario.getId(),
                StatusDivida.ATIVA
        );
        int geradas = 0;

        for (Divida divida : dividasAtivas) {
            int parcelasRestantes = Math.max(0, divida.getQuantidadeParcelas() - divida.getParcelasPagas());
            int limite = Math.min(quantidadeMeses, parcelasRestantes);

            for (int i = 0; i < limite; i++) {
                YearMonth competencia = competenciaInicial.plusMonths(i);

                boolean jaExiste = contaMensalRepository.existsByUsuarioIdAndDividaIdAndAnoAndMesAndAtivoTrue(
                        usuario.getId(),
                        divida.getId(),
                        competencia.getYear(),
                        competencia.getMonthValue()
                );

                if (jaExiste) {
                    continue;
                }

                ContaMensal conta = new ContaMensal();
                conta.setDescricao("Parcela - " + divida.getDescricao());
                conta.setValorPrevisto(divida.getValorParcela());
                conta.setDataVencimento(dataDaCompetencia(competencia, divida.getDiaVencimento()));
                conta.setMes(competencia.getMonthValue());
                conta.setAno(competencia.getYear());
                conta.setStatus(StatusContaMensal.PENDENTE);
                conta.setObservacao("Gerada automaticamente por dívida");
                conta.setAtivo(true);
                conta.setDivida(divida);
                conta.setUsuario(usuario);
                ContaMensal contaSalva = contaMensalRepository.save(conta);
                googleCalendarIntegrationService.sincronizarConta(contaSalva);
                geradas++;
            }
        }

        return geradas;
    }

    private LocalDate dataDaCompetencia(YearMonth competencia, Integer dia) {
        int diaAjustado = Math.min(dia, competencia.lengthOfMonth());
        return competencia.atDay(diaAjustado);
    }
}
