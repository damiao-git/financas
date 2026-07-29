package com.house.financas.repository;


import com.house.financas.model.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {

    List<Despesa> findByUsuarioIdAndAtivoTrueOrderByDiaVencimentoAsc(Long usuarioId);

    boolean existsByUsuarioIdAndAtivoTrue(Long usuarioId);

    List<Despesa> findByUsuarioIdAndRecorrenteTrueAndAtivoTrue(Long usuarioId);

    Optional<Despesa> findByIdAndUsuarioId(Long id, Long usuarioId);
}
