package com.house.financas.service;

import com.house.financas.dto.DespesaDto;
import com.house.financas.model.Categoria;
import com.house.financas.model.Despesa;
import com.house.financas.repository.CategoriaRepository;
import com.house.financas.repository.DespesaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DespesaService {
    private DespesaRepository repository;
    private CategoriaRepository categoriaRepository;
    private CategoriaService categoriaService;

    public Despesa buscarDespesaById(final Long id){
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Despesa não encontrada"));
    }

    public List<Despesa> listarTodasDespesas(){
        return repository.findAll();
    }

    public void cadastrarDespesa(final DespesaDto despesaDto){
        Optional<Categoria> categoria = categoriaRepository.findById(despesaDto.getCategoriaId());
        if(!categoria.isPresent()){
            throw new RuntimeException("Categoria não encontrada");
        }else{
            Despesa despesa = new Despesa();
            despesa.setDescricao(despesaDto.getDescricao());
            despesa.setDiaVencimento(despesaDto.getDiaVencimento());
            despesa.setCategoria(categoria.get());
            despesa.setDiaVencimento(despesaDto.getDiaVencimento());
            despesa.setValor(despesaDto.getValor());
            repository.save(despesa);
        }
    }

    public void atualizarDespesa(final Long id, final DespesaDto despesa){
        repository.findById(id)
                .map(c -> {
                    c.setCategoria(categoriaService.buscarPorId(despesa.getCategoriaId()));
                    c.setDiaVencimento(despesa.getDiaVencimento());
                    c.setDescricao(despesa.getDescricao());
                    c.setValor(despesa.getValor());
                    return repository.save(c);
                })
                .orElseThrow(() -> new RuntimeException("Despesa não encontrada"));
    }

    public void deletarDespesaById(final long id){
        Optional<Despesa> despesaEncontrada = repository.findById(id);
        if(despesaEncontrada.isEmpty()){
            throw new RuntimeException("Despesa não encontrada");
        }else{
            repository.deleteById(id);
        }
    }
}
