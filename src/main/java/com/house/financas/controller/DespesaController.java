package com.house.financas.controller;

import com.house.financas.dto.DespesaDto;
import com.house.financas.model.Despesa;
import com.house.financas.service.DespesaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/despesa")
@AllArgsConstructor
public class DespesaController {
    private DespesaService service;

    @GetMapping
    public ResponseEntity<List<Despesa>> listarTodasDespesas() {
        return service.listarTodasDespesas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Despesa> buscarDespesa(@PathVariable final Long id){
        return service.buscarDespesaById(id);
    }
    @PostMapping
    public ResponseEntity<?> cadastrarDespesa(@RequestBody final DespesaDto despesaDto) {
        return service.cadastrarDespesa(despesaDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Despesa> atualizarDespesa(@PathVariable final Long id, @RequestBody Despesa categoria) {
        return service.atualizarDespesa(id, categoria);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarDespesa(@PathVariable final Long id){
        return service.deletarDespesaById(id);
    }
}
