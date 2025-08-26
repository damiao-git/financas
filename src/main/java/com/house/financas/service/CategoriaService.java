package com.house.financas.service;

import com.house.financas.model.Categoria;
import com.house.financas.repository.CategoriaRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CategoriaService {

    private CategoriaRepository categoriaRepository;

    public ResponseEntity<Categoria> buscarCategoriaById(final Long id){
        Optional<Categoria> categoriaEncontrada = categoriaRepository.findById(id);
        return categoriaEncontrada.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public ResponseEntity<List<Categoria>> listarTodasCategorias(){
        return ResponseEntity.ok(categoriaRepository.findAll());
    }

    public ResponseEntity<Categoria> cadastrarCategoria(final Categoria categoria){
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaRepository.save(categoria));
    }

    public ResponseEntity<Categoria> atualizarCategoria(final Long id, final Categoria categoria){
        return categoriaRepository.findById(id)
                .map(c -> {
                    c.setNome(categoria.getNome());
                    return ResponseEntity.ok(categoriaRepository.save(c));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public ResponseEntity<Void> deletarCategoriaById(final long id){
        Optional<Categoria> categoriaEncontrada = categoriaRepository.findById(id);
        if(categoriaEncontrada.isPresent()){
            categoriaRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }
}
