package com.house.financas.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "despesa")
@RequiredArgsConstructor
@Data
public class Despesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descricao;
    private Double valor;
    private LocalDate data;

    @ManyToOne
    private Categoria categoria;
}
