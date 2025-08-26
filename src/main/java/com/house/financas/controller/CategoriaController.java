package com.house.financas.controller;

import com.house.financas.model.Categoria;
import com.house.financas.service.CategoriaService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categoria")
@AllArgsConstructor
public class CategoriaController {

    private CategoriaService service;

    @GetMapping
    public ResponseEntity<List<Categoria>> listarTodasCategorias() {
        return service.listarTodasCategorias();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscarCategoria(@PathVariable final Long id){
        return service.buscarCategoriaById(id);
    }
    @PostMapping
    public ResponseEntity<Categoria> cadastrarCategoria(@RequestBody final Categoria categoria) {
        return service.cadastrarCategoria(categoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> atualizarCategoria(@PathVariable final Long id, @RequestBody Categoria categoria) {
        return service.atualizarCategoria(id, categoria);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCategoria(@PathVariable final Long id){
        return service.deletarCategoriaById(id);
    }
}
