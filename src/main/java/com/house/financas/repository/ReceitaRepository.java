package com.house.financas.repository;

import com.house.financas.model.Receita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReceitaRepository extends JpaRepository<Receita, Long> {

    List<Receita> findByUsuarioIdAndAtivoTrueOrderByDiaRecebimentoAsc(Long usuarioId);

    Optional<Receita> findByIdAndUsuarioId(Long id, Long usuarioId);
}
