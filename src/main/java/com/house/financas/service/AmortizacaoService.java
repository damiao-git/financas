package com.house.financas.service;

import com.house.financas.dto.AmortizacaoRequest;
import com.house.financas.exception.DomainException;
import com.house.financas.exception.ResourceNotFoundException;
import com.house.financas.model.Amortizacao;
import com.house.financas.model.Divida;
import com.house.financas.model.Usuario;
import com.house.financas.model.enums.StatusDivida;
import com.house.financas.repository.AmortizacaoRepository;
import com.house.financas.repository.DividaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AmortizacaoService {

    private final AmortizacaoRepository amortizacaoRepository;
    private final DividaRepository dividaRepository;

    @Transactional(readOnly = true)
    public List<Amortizacao> listarPorDivida(Long dividaId, Usuario usuario) {
        buscarDividaAtiva(dividaId, usuario);
        return amortizacaoRepository.findByDividaIdAndUsuarioIdAndAtivoTrueOrderByDataAmortizacaoDesc(
                dividaId,
                usuario.getId()
        );
    }

    @Transactional(readOnly = true)
    public List<Amortizacao> listarPorCompetencia(Usuario usuario, Integer ano, Integer mes) {
        return amortizacaoRepository.findByUsuarioIdAndAnoAndMesAndAtivoTrueOrderByDataAmortizacaoDesc(
                usuario.getId(),
                ano,
                mes
        );
    }

    @Transactional(readOnly = true)
    public Amortizacao buscarPorId(Long id, Usuario usuario) {
        return amortizacaoRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Amortização não encontrada"));
    }

    public Amortizacao cadastrar(Long dividaId, AmortizacaoRequest request, Usuario usuario) {
        Divida divida = buscarDividaAtiva(dividaId, usuario);
        validarValorNaoUltrapassaSaldo(request.getValor(), divida.getSaldoAtual());

        Amortizacao amortizacao = new Amortizacao();
        preencherAmortizacao(amortizacao, request);
        amortizacao.setDivida(divida);
        amortizacao.setUsuario(usuario);
        amortizacao.setAtivo(true);

        aplicarAmortizacao(divida, request.getValor());
        dividaRepository.save(divida);

        return amortizacaoRepository.save(amortizacao);
    }

    public Amortizacao atualizar(Long id, AmortizacaoRequest request, Usuario usuario) {
        Amortizacao amortizacao = buscarPorId(id, usuario);
        validarAmortizacaoAtiva(amortizacao);

        Divida divida = amortizacao.getDivida();
        BigDecimal diferenca = request.getValor().subtract(amortizacao.getValor());
        BigDecimal novoSaldo = divida.getSaldoAtual().subtract(diferenca);

        if (novoSaldo.signum() < 0) {
            throw new DomainException("Valor da amortização não pode ser maior que saldo atual da dívida");
        }

        preencherAmortizacao(amortizacao, request);
        divida.setSaldoAtual(novoSaldo);
        atualizarStatusDivida(divida);
        dividaRepository.save(divida);

        return amortizacaoRepository.save(amortizacao);
    }

    public void deletar(Long id, Usuario usuario) {
        Amortizacao amortizacao = buscarPorId(id, usuario);
        validarAmortizacaoAtiva(amortizacao);

        Divida divida = amortizacao.getDivida();
        amortizacao.setAtivo(false);
        divida.setSaldoAtual(divida.getSaldoAtual().add(amortizacao.getValor()));
        atualizarStatusDivida(divida);

        dividaRepository.save(divida);
        amortizacaoRepository.save(amortizacao);
    }

    private Divida buscarDividaAtiva(Long dividaId, Usuario usuario) {
        Divida divida = dividaRepository.findByIdAndUsuarioId(dividaId, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Dívida não encontrada"));

        if (!Boolean.TRUE.equals(divida.getAtivo()) || StatusDivida.CANCELADA.equals(divida.getStatus())) {
            throw new DomainException("Dívida não está ativa");
        }

        return divida;
    }

    private void preencherAmortizacao(Amortizacao amortizacao, AmortizacaoRequest request) {
        amortizacao.setValor(request.getValor());
        amortizacao.setDataAmortizacao(request.getDataAmortizacao());
        amortizacao.setMes(request.getMes());
        amortizacao.setAno(request.getAno());
        amortizacao.setObservacao(normalizarTextoOpcional(request.getObservacao()));
    }

    private void aplicarAmortizacao(Divida divida, BigDecimal valor) {
        divida.setSaldoAtual(divida.getSaldoAtual().subtract(valor));
        atualizarStatusDivida(divida);
    }

    private void atualizarStatusDivida(Divida divida) {
        if (BigDecimal.ZERO.compareTo(divida.getSaldoAtual()) == 0) {
            divida.setStatus(StatusDivida.QUITADA);
            return;
        }

        divida.setStatus(StatusDivida.ATIVA);
    }

    private void validarValorNaoUltrapassaSaldo(BigDecimal valor, BigDecimal saldoAtual) {
        if (valor.compareTo(saldoAtual) > 0) {
            throw new DomainException("Valor da amortização não pode ser maior que saldo atual da dívida");
        }
    }

    private void validarAmortizacaoAtiva(Amortizacao amortizacao) {
        if (!Boolean.TRUE.equals(amortizacao.getAtivo())) {
            throw new DomainException("Amortização já está inativa");
        }
    }

    private String normalizarTextoOpcional(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
