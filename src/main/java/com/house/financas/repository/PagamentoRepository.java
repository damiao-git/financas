package com.house.financas.repository;

import com.house.financas.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByContaMensalIdAndUsuarioIdAndAtivoTrueOrderByDataPagamentoDesc(
            Long contaMensalId,
            Long usuarioId
    );

    boolean existsByContaMensalIdAndUsuarioIdAndAtivoTrue(Long contaMensalId, Long usuarioId);

    Optional<Pagamento> findByIdAndUsuarioId(Long id, Long usuarioId);
}
