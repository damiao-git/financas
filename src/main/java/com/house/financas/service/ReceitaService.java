package com.house.financas.service;

import com.house.financas.dto.ReceitaRequest;
import com.house.financas.exception.ResourceNotFoundException;
import com.house.financas.model.Receita;
import com.house.financas.model.Usuario;
import com.house.financas.repository.ReceitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReceitaService {

    private final ReceitaRepository receitaRepository;

    @Transactional(readOnly = true)
    public List<Receita> listar(Usuario usuario) {
        return receitaRepository.findByUsuarioIdAndAtivoTrueOrderByDiaRecebimentoAsc(usuario.getId());
    }

    @Transactional(readOnly = true)
    public Receita buscarPorId(Long id, Usuario usuario) {
        return receitaRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Receita nao encontrada"));
    }

    public Receita cadastrar(ReceitaRequest request, Usuario usuario) {
        Receita receita = new Receita();
        preencherReceita(receita, request);
        receita.setUsuario(usuario);
        receita.setAtivo(true);

        return receitaRepository.save(receita);
    }

    public Receita atualizar(Long id, ReceitaRequest request, Usuario usuario) {
        Receita receita = buscarPorId(id, usuario);
        preencherReceita(receita, request);

        return receitaRepository.save(receita);
    }

    public void deletar(Long id, Usuario usuario) {
        Receita receita = buscarPorId(id, usuario);
        receita.setAtivo(false);
        receitaRepository.save(receita);
    }

    private void preencherReceita(Receita receita, ReceitaRequest request) {
        receita.setDescricao(request.getDescricao().trim());
        receita.setValor(request.getValor());
        receita.setDiaRecebimento(request.getDiaRecebimento());
        receita.setTipoReceita(request.getTipoReceita());
        receita.setOrigem(request.getOrigem().trim());
        receita.setRecorrente(request.getRecorrente());
    }
}
