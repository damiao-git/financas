package com.house.financas.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.MonthDay;

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
