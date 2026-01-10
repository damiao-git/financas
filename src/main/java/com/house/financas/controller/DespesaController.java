package com.house.financas.controller;

import com.house.financas.dto.DespesaDto;
import com.house.financas.model.Despesa;
import com.house.financas.service.DespesaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/despesas")
@AllArgsConstructor
public class DespesaController {
    private DespesaService service;

    @GetMapping
    public ResponseEntity<List<Despesa>> listarTodasDespesas() {
        return ResponseEntity.ok(service.listarTodasDespesas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Despesa> buscarDespesa(@PathVariable final Long id){
        return ResponseEntity.ok(service.buscarDespesaById(id));
    }
    @PostMapping
    public ResponseEntity<?> cadastrarDespesa(@RequestBody final DespesaDto despesaDto) {
        service.cadastrarDespesa(despesaDto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Despesa> atualizarDespesa(@PathVariable final Long id, @RequestBody DespesaDto categoria) {
        service.atualizarDespesa(id, categoria);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarDespesa(@PathVariable final Long id){
        service.deletarDespesaById(id);
        return ResponseEntity.noContent().build();
    }
}
