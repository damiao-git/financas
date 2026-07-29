package com.house.financas.service;

import com.house.financas.dto.CategoriaRequest;
import com.house.financas.exception.ResourceNotFoundException;
import com.house.financas.model.Categoria;
import com.house.financas.model.Usuario;
import com.house.financas.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<Categoria> listar(Usuario usuario) {
        return categoriaRepository.findByUsuarioIdAndAtivoTrueOrderByNomeAsc(usuario.getId());
    }

    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id, Usuario usuario) {
        return categoriaRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
    }

    public Categoria cadastrar(CategoriaRequest request, Usuario usuario) {
        Categoria categoria = new Categoria();
        categoria.setNome(request.getNome().trim());
        categoria.setUsuario(usuario);
        categoria.setAtivo(true);

        return categoriaRepository.save(categoria);
    }

    public Categoria atualizar(Long id, CategoriaRequest request, Usuario usuario) {
        Categoria categoria = buscarPorId(id, usuario);
        categoria.setNome(request.getNome().trim());

        return categoriaRepository.save(categoria);
    }

    public void deletar(Long id, Usuario usuario) {
        Categoria categoria = buscarPorId(id, usuario);
        categoria.setAtivo(false);
        categoriaRepository.save(categoria);
    }
}
