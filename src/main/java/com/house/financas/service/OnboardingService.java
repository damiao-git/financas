package com.house.financas.service;

import com.house.financas.dto.OnboardingStatusResponse;
import com.house.financas.model.Usuario;
import com.house.financas.model.enums.OnboardingPasso;
import com.house.financas.repository.ContaMensalRepository;
import com.house.financas.repository.DespesaRepository;
import com.house.financas.repository.DividaRepository;
import com.house.financas.repository.ReceitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final ReceitaRepository receitaRepository;
    private final ContaMensalRepository contaMensalRepository;
    private final DespesaRepository despesaRepository;
    private final DividaRepository dividaRepository;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public OnboardingStatusResponse status(Usuario usuario) {
        Long usuarioId = usuario.getId();
        boolean possuiReceita = receitaRepository.existsByUsuarioIdAndAtivoTrue(usuarioId);
        boolean possuiContaOuDespesa = contaMensalRepository.existsByUsuarioIdAndAtivoTrue(usuarioId)
                || despesaRepository.existsByUsuarioIdAndAtivoTrue(usuarioId);
        boolean possuiDivida = dividaRepository.existsByUsuarioIdAndAtivoTrue(usuarioId);
        boolean concluido = Boolean.TRUE.equals(usuario.getOnboardingConcluido())
                || (possuiReceita && possuiContaOuDespesa);

        return new OnboardingStatusResponse(
                concluido,
                possuiReceita,
                possuiContaOuDespesa,
                possuiDivida,
                proximoPasso(concluido, possuiReceita, possuiContaOuDespesa).name()
        );
    }

    @Transactional
    public OnboardingStatusResponse concluir(Usuario usuario) {
        Usuario atualizado = usuarioService.concluirOnboarding(usuario);
        return status(atualizado);
    }

    private OnboardingPasso proximoPasso(boolean concluido, boolean possuiReceita, boolean possuiContaOuDespesa) {
        if (concluido) {
            return OnboardingPasso.CONCLUIDO;
        }
        if (!possuiReceita) {
            return OnboardingPasso.CADASTRAR_RECEITA;
        }
        if (!possuiContaOuDespesa) {
            return OnboardingPasso.CADASTRAR_CONTA;
        }
        return OnboardingPasso.CADASTRAR_DIVIDA;
    }
}
