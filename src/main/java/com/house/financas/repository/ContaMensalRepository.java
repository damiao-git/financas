package com.house.financas.repository;

import com.house.financas.model.ContaMensal;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
