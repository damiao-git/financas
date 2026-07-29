package com.house.financas.service;

import com.house.financas.dto.ContaMensalRequest;
import com.house.financas.exception.ResourceNotFoundException;
import com.house.financas.model.Categoria;
import com.house.financas.model.ContaMensal;
import com.house.financas.model.Despesa;
import com.house.financas.model.Usuario;
import com.house.financas.model.enums.StatusContaMensal;
import com.house.financas.repository.CategoriaRepository;
import com.house.financas.repository.ContaMensalRepository;
import com.house.financas.repository.DespesaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ContaMensalService {

    private final ContaMensalRepository contaMensalRepository;
    private final CategoriaRepository categoriaRepository;
    private final DespesaRepository despesaRepository;
    private final GoogleCalendarIntegrationService googleCalendarIntegrationService;

    @Transactional(readOnly = true)
    public List<ContaMensal> listar(Usuario usuario, Integer ano, Integer mes) {
        if (ano != null && mes != null) {
            return contaMensalRepository.findByUsuarioIdAndAnoAndMesAndAtivoTrueOrderByDataVencimentoAsc(
                    usuario.getId(),
                    ano,
                    mes
            );
        }

        return contaMensalRepository.findByUsuarioIdAndAtivoTrueOrderByDataVencimentoAsc(usuario.getId());
    }

    @Transactional(readOnly = true)
    public ContaMensal buscarPorId(Long id, Usuario usuario) {
        return contaMensalRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Conta mensal não encontrada"));
    }

    public ContaMensal cadastrar(ContaMensalRequest request, Usuario usuario) {
        ContaMensal contaMensal = new ContaMensal();
        preencherContaMensal(contaMensal, request, usuario);
        contaMensal.setUsuario(usuario);
        contaMensal.setStatus(StatusContaMensal.PENDENTE);
        contaMensal.setAtivo(true);

        ContaMensal contaSalva = contaMensalRepository.save(contaMensal);
        googleCalendarIntegrationService.sincronizarConta(contaSalva);

        return contaSalva;
    }

    public ContaMensal atualizar(Long id, ContaMensalRequest request, Usuario usuario) {
        ContaMensal contaMensal = buscarPorId(id, usuario);
        preencherContaMensal(contaMensal, request, usuario);

        ContaMensal contaSalva = contaMensalRepository.save(contaMensal);
        googleCalendarIntegrationService.sincronizarConta(contaSalva);

        return contaSalva;
    }

    public void cancelar(Long id, Usuario usuario) {
        ContaMensal contaMensal = buscarPorId(id, usuario);
        contaMensal.setStatus(StatusContaMensal.CANCELADA);
        contaMensal.setAtivo(false);
        contaMensalRepository.save(contaMensal);
        googleCalendarIntegrationService.sincronizarConta(contaMensal);
    }

    public void marcarComoPaga(ContaMensal contaMensal) {
        contaMensal.setStatus(StatusContaMensal.PAGA);
        contaMensalRepository.save(contaMensal);
        googleCalendarIntegrationService.sincronizarConta(contaMensal);
    }

    public void marcarComoPendente(ContaMensal contaMensal) {
        contaMensal.setStatus(StatusContaMensal.PENDENTE);
        contaMensalRepository.save(contaMensal);
        googleCalendarIntegrationService.sincronizarConta(contaMensal);
    }

    private void preencherContaMensal(ContaMensal contaMensal, ContaMensalRequest request, Usuario usuario) {
        contaMensal.setDescricao(request.getDescricao().trim());
        contaMensal.setValorPrevisto(request.getValorPrevisto());
        contaMensal.setDataVencimento(request.getDataVencimento());
        contaMensal.setMes(request.getMes());
        contaMensal.setAno(request.getAno());
        contaMensal.setObservacao(normalizarTextoOpcional(request.getObservacao()));
        contaMensal.setCategoria(buscarCategoria(request.getCategoriaId(), usuario));
        contaMensal.setDespesa(buscarDespesa(request.getDespesaId(), usuario));
    }

    private Categoria buscarCategoria(Long categoriaId, Usuario usuario) {
        if (categoriaId == null) {
            return null;
        }

        return categoriaRepository.findByIdAndUsuarioId(categoriaId, usuario.getId())
                .filter(categoria -> Boolean.TRUE.equals(categoria.getAtivo()))
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
    }

    private Despesa buscarDespesa(Long despesaId, Usuario usuario) {
        if (despesaId == null) {
            return null;
        }

        return despesaRepository.findByIdAndUsuarioId(despesaId, usuario.getId())
                .filter(despesa -> Boolean.TRUE.equals(despesa.getAtivo()))
                .orElseThrow(() -> new ResourceNotFoundException("Despesa não encontrada"));
    }

    private String normalizarTextoOpcional(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
