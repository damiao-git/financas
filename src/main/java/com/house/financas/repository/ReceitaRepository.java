package com.house.financas.repository;

import com.house.financas.model.Receita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ReceitaRepository extends JpaRepository<Receita, Long> {

    List<Receita> findByUsuarioIdAndAtivoTrueOrderByDiaRecebimentoAsc(Long usuarioId);

    List<Receita> findByUsuarioIdAndAnoAndMesAndAtivoTrueOrderByDiaRecebimentoAsc(
            Long usuarioId,
            Integer ano,
            Integer mes
    );

    Optional<Receita> findByIdAndUsuarioId(Long id, Long usuarioId);

    @Query("""
            select coalesce(sum(receita.valor), 0)
            from Receita receita
            where receita.usuario.id = :usuarioId
              and receita.ano = :ano
              and receita.mes = :mes
              and receita.ativo = true
            """)
    BigDecimal somarReceitasAtivasPorCompetencia(
            @Param("usuarioId") Long usuarioId,
            @Param("ano") Integer ano,
            @Param("mes") Integer mes
    );
}
