package com.house.financas.service;

import com.house.financas.dto.PagamentoRequest;
import com.house.financas.exception.ResourceNotFoundException;
import com.house.financas.model.ContaMensal;
import com.house.financas.model.Pagamento;
import com.house.financas.model.Usuario;
import com.house.financas.repository.PagamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final ContaMensalService contaMensalService;

    @Transactional(readOnly = true)
    public List<Pagamento> listarPorConta(Long contaMensalId, Usuario usuario) {
        contaMensalService.buscarPorId(contaMensalId, usuario);
        return pagamentoRepository.findByContaMensalIdAndUsuarioIdAndAtivoTrueOrderByDataPagamentoDesc(
                contaMensalId,
                usuario.getId()
        );
    }

    @Transactional(readOnly = true)
    public Pagamento buscarPorId(Long id, Usuario usuario) {
        return pagamentoRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado"));
    }

    public Pagamento cadastrar(Long contaMensalId, PagamentoRequest request, Usuario usuario) {
        ContaMensal contaMensal = contaMensalService.buscarPorId(contaMensalId, usuario);

        Pagamento pagamento = new Pagamento();
        preencherPagamento(pagamento, request);
        pagamento.setContaMensal(contaMensal);
        pagamento.setUsuario(usuario);
        pagamento.setAtivo(true);

        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);
        contaMensalService.marcarComoPaga(contaMensal);

        return pagamentoSalvo;
    }

    public Pagamento atualizar(Long id, PagamentoRequest request, Usuario usuario) {
        Pagamento pagamento = buscarPorId(id, usuario);
        preencherPagamento(pagamento, request);

        return pagamentoRepository.save(pagamento);
    }

    public void deletar(Long id, Usuario usuario) {
        Pagamento pagamento = buscarPorId(id, usuario);
        pagamento.setAtivo(false);
        pagamentoRepository.save(pagamento);

        ContaMensal contaMensal = pagamento.getContaMensal();
        boolean possuiOutroPagamentoAtivo = pagamentoRepository.existsByContaMensalIdAndUsuarioIdAndAtivoTrue(
                contaMensal.getId(),
                usuario.getId()
        );

        if (!possuiOutroPagamentoAtivo) {
            contaMensalService.marcarComoPendente(contaMensal);
        }
    }

    private void preencherPagamento(Pagamento pagamento, PagamentoRequest request) {
        pagamento.setValorPago(request.getValorPago());
        pagamento.setDataPagamento(request.getDataPagamento());
        pagamento.setObservacao(normalizarTextoOpcional(request.getObservacao()));
    }

    private String normalizarTextoOpcional(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
