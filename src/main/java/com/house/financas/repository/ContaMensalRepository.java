package com.house.financas.repository;

import com.house.financas.model.ContaMensal;
import com.house.financas.model.enums.StatusContaMensal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ContaMensalRepository extends JpaRepository<ContaMensal, Long> {

    List<ContaMensal> findByUsuarioIdAndAtivoTrueOrderByDataVencimentoAsc(Long usuarioId);

    List<ContaMensal> findByUsuarioIdAndAnoAndMesAndAtivoTrueOrderByDataVencimentoAsc(
            Long usuarioId,
            Integer ano,
            Integer mes
    );

    Optional<ContaMensal> findByIdAndUsuarioId(Long id, Long usuarioId);

    boolean existsByUsuarioIdAndDespesaIdAndAnoAndMesAndAtivoTrue(
            Long usuarioId,
            Long despesaId,
            Integer ano,
            Integer mes
    );

    boolean existsByUsuarioIdAndDividaIdAndAnoAndMesAndAtivoTrue(
            Long usuarioId,
            Long dividaId,
            Integer ano,
            Integer mes
    );

    @Query("""
            select coalesce(sum(conta.valorPrevisto), 0)
            from ContaMensal conta
            where conta.usuario.id = :usuarioId
              and conta.ano = :ano
              and conta.mes = :mes
              and conta.ativo = true
            """)
    BigDecimal somarValorPrevistoPorMes(
            @Param("usuarioId") Long usuarioId,
            @Param("ano") Integer ano,
            @Param("mes") Integer mes
    );

    @Query("""
            select coalesce(sum(conta.valorPrevisto), 0)
            from ContaMensal conta
            where conta.usuario.id = :usuarioId
              and conta.ano = :ano
              and conta.mes = :mes
              and conta.status = :status
              and conta.ativo = true
            """)
    BigDecimal somarValorPrevistoPorStatus(
            @Param("usuarioId") Long usuarioId,
            @Param("ano") Integer ano,
            @Param("mes") Integer mes,
            @Param("status") StatusContaMensal status
    );
}
