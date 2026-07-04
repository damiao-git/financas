package com.house.financas.service;

import com.house.financas.dto.RegisterRequest;
import com.house.financas.dto.UsuarioUpdateRequest;
import com.house.financas.exception.DomainException;
import com.house.financas.exception.ResourceNotFoundException;
import com.house.financas.model.Usuario;
import com.house.financas.repository.UsuarioRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario cadastrar(RegisterRequest request) {
        return cadastrar(request.getNome(), request.getEmail(), request.getSenha());
    }

    public Usuario cadastrar(String nome, String email, String senha) {
        String emailNormalizado = normalizarEmail(email);

        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            throw new DomainException("Email ja cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome.trim());
        usuario.setEmail(emailNormalizado);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setAtivo(true);

        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario autenticar(String email, String senha) {
        String emailNormalizado = normalizarEmail(email);

        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new BadCredentialsException("Email ou senha invalidos"));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new BadCredentialsException("Usuario inativo");
        }

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new BadCredentialsException("Email ou senha invalidos");
        }

        return usuario;
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado"));
    }

    public Usuario atualizar(Long id, UsuarioUpdateRequest request) {
        Usuario usuario = buscarPorId(id);

        if (request.getNome() != null && !request.getNome().isBlank()) {
            usuario.setNome(request.getNome().trim());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String emailNormalizado = normalizarEmail(request.getEmail());
            usuarioRepository.findByEmail(emailNormalizado)
                    .filter(encontrado -> !encontrado.getId().equals(id))
                    .ifPresent(encontrado -> {
                        throw new DomainException("Email ja cadastrado");
                    });
            usuario.setEmail(emailNormalizado);
        }

        if (request.getSenha() != null && !request.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        }

        if (request.getAtivo() != null) {
            usuario.setAtivo(request.getAtivo());
        }

        return usuarioRepository.save(usuario);
    }

    public void deletar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }
}
