package com.house.financas.controller;

import com.house.financas.dto.PagamentoRequest;
import com.house.financas.dto.PagamentoResponse;
import com.house.financas.model.Pagamento;
import com.house.financas.model.Usuario;
import com.house.financas.service.PagamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @GetMapping("/contas-mensais/{contaMensalId}/pagamentos")
    public ResponseEntity<List<PagamentoResponse>> listarPorConta(
            @PathVariable Long contaMensalId,
            @AuthenticationPrincipal Usuario usuario) {

        List<PagamentoResponse> pagamentos = pagamentoService.listarPorConta(contaMensalId, usuario)
                .stream()
                .map(PagamentoResponse::from)
                .toList();

        return ResponseEntity.ok(pagamentos);
    }

    @PostMapping("/contas-mensais/{contaMensalId}/pagamentos")
    public ResponseEntity<PagamentoResponse> cadastrar(
            @PathVariable Long contaMensalId,
            @RequestBody @Valid PagamentoRequest request,
            @AuthenticationPrincipal Usuario usuario) {

        Pagamento pagamento = pagamentoService.cadastrar(contaMensalId, request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(PagamentoResponse.from(pagamento));
    }

    @PutMapping("/pagamentos/{id}")
    public ResponseEntity<PagamentoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid PagamentoRequest request,
            @AuthenticationPrincipal Usuario usuario) {

        Pagamento pagamento = pagamentoService.atualizar(id, request, usuario);
        return ResponseEntity.ok(PagamentoResponse.from(pagamento));
    }

    @DeleteMapping("/pagamentos/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        pagamentoService.deletar(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
