package com.house.financas.controller;

import com.house.financas.model.Categoria;
import com.house.financas.service.CategoriaService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categoria")
@AllArgsConstructor
public class CategoriaController {

    private CategoriaService service;

    @GetMapping
    public ResponseEntity<List<Categoria>> listarTodasCategorias(){
        return service.listarTodasCategorias();
    }

}
