package com.house.financas.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;


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
    private Long diaVencimento;

    @ManyToOne
    private Categoria categoria;
}
