package com.house.financas.service;

import com.house.financas.dto.DespesaRequest;
import com.house.financas.exception.ResourceNotFoundException;
import com.house.financas.model.Categoria;
import com.house.financas.model.Despesa;
import com.house.financas.model.Usuario;
import com.house.financas.repository.CategoriaRepository;
import com.house.financas.repository.DespesaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DespesaService {

    private final DespesaRepository despesaRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<Despesa> listar(Usuario usuario) {
        return despesaRepository.findByUsuarioIdAndAtivoTrueOrderByDiaVencimentoAsc(usuario.getId());
    }

    @Transactional(readOnly = true)
    public Despesa buscarPorId(Long id, Usuario usuario) {
        return despesaRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Despesa nao encontrada"));
    }

    public Despesa cadastrar(DespesaRequest request, Usuario usuario) {
        Categoria categoria = buscarCategoriaDoUsuario(request.getCategoriaId(), usuario);

        Despesa despesa = new Despesa();
        preencherDespesa(despesa, request, categoria);
        despesa.setUsuario(usuario);
        despesa.setAtivo(true);

        return despesaRepository.save(despesa);
    }

    public Despesa atualizar(Long id, DespesaRequest request, Usuario usuario) {
        Despesa despesa = buscarPorId(id, usuario);
        Categoria categoria = buscarCategoriaDoUsuario(request.getCategoriaId(), usuario);
        preencherDespesa(despesa, request, categoria);

        return despesaRepository.save(despesa);
    }

    public void deletar(Long id, Usuario usuario) {
        Despesa despesa = buscarPorId(id, usuario);
        despesa.setAtivo(false);
        despesaRepository.save(despesa);
    }

    private Categoria buscarCategoriaDoUsuario(Long categoriaId, Usuario usuario) {
        return categoriaRepository.findByIdAndUsuarioId(categoriaId, usuario.getId())
                .filter(categoria -> Boolean.TRUE.equals(categoria.getAtivo()))
                .orElseThrow(() -> new ResourceNotFoundException("Categoria nao encontrada"));
    }

    private void preencherDespesa(Despesa despesa, DespesaRequest request, Categoria categoria) {
        despesa.setDescricao(request.getDescricao().trim());
        despesa.setValor(request.getValor());
        despesa.setDiaVencimento(request.getDiaVencimento());
        despesa.setTipoDespesa(request.getTipoDespesa());
        despesa.setRecorrente(request.getRecorrente());
        despesa.setCategoria(categoria);
    }
}
