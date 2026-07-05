package com.house.financas.model;

import com.house.financas.model.enums.StatusDivida;
import com.house.financas.model.enums.TipoDivida;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_DIVIDAS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Divida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String descricao;

    @Column(nullable = false, length = 150)
    private String instituicao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoDivida tipoDivida;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal saldoInicial;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal saldoAtual;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorParcela;

    @Column(nullable = false)
    private Integer diaVencimento;

    @Column(nullable = false)
    private Integer quantidadeParcelas;

    @Column(nullable = false)
    private Integer parcelasPagas;

    @Column(precision = 5, scale = 2)
    private BigDecimal taxaJurosMensal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusDivida status = StatusDivida.ATIVA;

    @Column(length = 255)
    private String observacao;

    @Column(nullable = false)
    private Boolean ativo = true;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }
}
