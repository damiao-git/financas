package com.house.financas.service;

import com.house.financas.dto.DespesaDto;
import com.house.financas.exception.DomainException;
import com.house.financas.model.Categoria;
import com.house.financas.model.Despesa;
import com.house.financas.repository.CategoriaRepository;
import com.house.financas.repository.DespesaRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DespesaService {
    private DespesaRepository repository;
    private CategoriaRepository categoriaRepository;

    public ResponseEntity<Despesa> buscarDespesaById(final Long id){
        Optional<Despesa> despesaEncontrada = repository.findById(id);
        return despesaEncontrada.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public ResponseEntity<List<Despesa>> listarTodasDespesas(){
        return ResponseEntity.ok(repository.findAll());
    }

    public ResponseEntity<?> cadastrarDespesa(final DespesaDto despesaDto){
        Optional<Categoria> categoria = categoriaRepository.findById(despesaDto.getCategoriaId());
        if(categoria.isPresent()){
            Despesa despesa = new Despesa();
            despesa.setDescricao(despesaDto.getDescricao());
            despesa.setDiaVencimento(despesaDto.getDiaVencimento());
            despesa.setCategoria(categoria.get());
            despesa.setDiaVencimento(despesaDto.getDiaVencimento());
            despesa.setValor(despesaDto.getValor());
            return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(despesa));
        }else{
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Categoria não encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    public ResponseEntity<Despesa> atualizarDespesa(final Long id, final Despesa despesa){
        return repository.findById(id)
                .map(c -> {
                    c.setCategoria(despesa.getCategoria());
                    c.setDiaVencimento(despesa.getDiaVencimento());
                    c.setDescricao(despesa.getDescricao());
                    c.setValor(despesa.getValor());
                    return ResponseEntity.ok(repository.save(c));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public ResponseEntity<Void> deletarDespesaById(final long id){
        Optional<Despesa> despesaEncontrada = repository.findById(id);
        if(despesaEncontrada.isPresent()){
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }
}
