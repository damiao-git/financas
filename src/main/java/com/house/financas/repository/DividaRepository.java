package com.house.financas.repository;

import com.house.financas.model.Divida;
import com.house.financas.model.enums.StatusDivida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DividaRepository extends JpaRepository<Divida, Long> {

    List<Divida> findByUsuarioIdAndAtivoTrueOrderByStatusAscDescricaoAsc(Long usuarioId);

    List<Divida> findByUsuarioIdAndStatusAndAtivoTrueOrderByDescricaoAsc(Long usuarioId, StatusDivida status);

    Optional<Divida> findByIdAndUsuarioId(Long id, Long usuarioId);
}
