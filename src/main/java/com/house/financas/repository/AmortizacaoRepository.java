package com.house.financas.repository;

import com.house.financas.model.Amortizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AmortizacaoRepository extends JpaRepository<Amortizacao, Long> {

    List<Amortizacao> findByDividaIdAndUsuarioIdAndAtivoTrueOrderByDataAmortizacaoDesc(
            Long dividaId,
            Long usuarioId
    );

    List<Amortizacao> findByUsuarioIdAndAnoAndMesAndAtivoTrueOrderByDataAmortizacaoDesc(
            Long usuarioId,
            Integer ano,
            Integer mes
    );

    Optional<Amortizacao> findByIdAndUsuarioId(Long id, Long usuarioId);

    @Query("""
            select coalesce(sum(amortizacao.valor), 0)
            from Amortizacao amortizacao
            where amortizacao.usuario.id = :usuarioId
              and amortizacao.ano = :ano
              and amortizacao.mes = :mes
              and amortizacao.ativo = true
            """)
    BigDecimal somarAmortizacoesAtivasPorCompetencia(
            @Param("usuarioId") Long usuarioId,
            @Param("ano") Integer ano,
            @Param("mes") Integer mes
    );
}
