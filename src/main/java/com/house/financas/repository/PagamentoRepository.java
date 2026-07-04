package com.house.financas.repository;

import com.house.financas.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByContaMensalIdAndUsuarioIdAndAtivoTrueOrderByDataPagamentoDesc(
            Long contaMensalId,
            Long usuarioId
    );

    boolean existsByContaMensalIdAndUsuarioIdAndAtivoTrue(Long contaMensalId, Long usuarioId);

    Optional<Pagamento> findByIdAndUsuarioId(Long id, Long usuarioId);

    @Query("""
            select coalesce(sum(pagamento.valorPago), 0)
            from Pagamento pagamento
            where pagamento.usuario.id = :usuarioId
              and pagamento.contaMensal.ano = :ano
              and pagamento.contaMensal.mes = :mes
              and pagamento.contaMensal.ativo = true
              and pagamento.ativo = true
            """)
    BigDecimal somarPagamentosAtivosPorMes(
            @Param("usuarioId") Long usuarioId,
            @Param("ano") Integer ano,
            @Param("mes") Integer mes
    );
}
