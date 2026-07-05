package com.house.financas.service;

import com.house.financas.dto.DividaRequest;
import com.house.financas.exception.DomainException;
import com.house.financas.exception.ResourceNotFoundException;
import com.house.financas.model.Divida;
import com.house.financas.model.Usuario;
import com.house.financas.model.enums.StatusDivida;
import com.house.financas.repository.DividaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DividaService {

    private final DividaRepository dividaRepository;

    @Transactional(readOnly = true)
    public List<Divida> listar(Usuario usuario, StatusDivida status) {
        if (status != null) {
            return dividaRepository.findByUsuarioIdAndStatusAndAtivoTrueOrderByDescricaoAsc(
                    usuario.getId(),
                    status
            );
        }

        return dividaRepository.findByUsuarioIdAndAtivoTrueOrderByStatusAscDescricaoAsc(usuario.getId());
    }

    @Transactional(readOnly = true)
    public Divida buscarPorId(Long id, Usuario usuario) {
        return dividaRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Divida nao encontrada"));
    }

    public Divida cadastrar(DividaRequest request, Usuario usuario) {
        validarRequest(request);

        Divida divida = new Divida();
        preencherDivida(divida, request);
        divida.setUsuario(usuario);
        divida.setAtivo(true);
        atualizarStatus(divida);

        return dividaRepository.save(divida);
    }

    public Divida atualizar(Long id, DividaRequest request, Usuario usuario) {
        validarRequest(request);

        Divida divida = buscarPorId(id, usuario);
        preencherDivida(divida, request);
        atualizarStatus(divida);

        return dividaRepository.save(divida);
    }

    public void cancelar(Long id, Usuario usuario) {
        Divida divida = buscarPorId(id, usuario);
        divida.setStatus(StatusDivida.CANCELADA);
        divida.setAtivo(false);
        dividaRepository.save(divida);
    }

    private void preencherDivida(Divida divida, DividaRequest request) {
        divida.setDescricao(request.getDescricao().trim());
        divida.setInstituicao(request.getInstituicao().trim());
        divida.setTipoDivida(request.getTipoDivida());
        divida.setSaldoInicial(request.getSaldoInicial());
        divida.setSaldoAtual(request.getSaldoAtual());
        divida.setValorParcela(request.getValorParcela());
        divida.setDiaVencimento(request.getDiaVencimento());
        divida.setQuantidadeParcelas(request.getQuantidadeParcelas());
        divida.setParcelasPagas(request.getParcelasPagas());
        divida.setTaxaJurosMensal(request.getTaxaJurosMensal());
        divida.setObservacao(normalizarTextoOpcional(request.getObservacao()));
    }

    private void atualizarStatus(Divida divida) {
        if (BigDecimal.ZERO.compareTo(divida.getSaldoAtual()) == 0) {
            divida.setStatus(StatusDivida.QUITADA);
            return;
        }

        divida.setStatus(StatusDivida.ATIVA);
    }

    private void validarRequest(DividaRequest request) {
        if (request.getSaldoAtual().compareTo(request.getSaldoInicial()) > 0) {
            throw new DomainException("Saldo atual nao pode ser maior que saldo inicial");
        }

        if (request.getParcelasPagas() > request.getQuantidadeParcelas()) {
            throw new DomainException("Parcelas pagas nao pode ser maior que quantidade de parcelas");
        }
    }

    private String normalizarTextoOpcional(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
