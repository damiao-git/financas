package com.house.financas.service;

import com.house.financas.dto.RegisterRequest;
import com.house.financas.dto.UsuarioUpdateRequest;
import com.house.financas.exception.DomainException;
import com.house.financas.exception.ResourceNotFoundException;
import com.house.financas.model.Usuario;
import com.house.financas.model.enums.RoleUsuario;
import com.house.financas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final Set<String> adminEmails;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          @Value("${monexa.admin.emails:}") String adminEmails) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmails = Arrays.stream(adminEmails.split(","))
                .map(String::trim)
                .filter(email -> !email.isBlank())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
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
        usuario.setRole(roleParaEmail(emailNormalizado));

        return usuarioRepository.save(usuario);
    }

    public Usuario autenticarGoogle(String nome, String email) {
        String emailNormalizado = normalizarEmail(email);

        return usuarioRepository.findByEmail(emailNormalizado)
                .map(usuario -> {
                    if (!Boolean.TRUE.equals(usuario.getAtivo())) {
                        usuario.setAtivo(true);
                    }
                    if (usuario.getOnboardingConcluido() == null) {
                        usuario.setOnboardingConcluido(false);
                    }
                    if (usuario.getNome() == null || usuario.getNome().isBlank()) {
                        usuario.setNome(nome.trim());
                    }
                    garantirAdminConfigurado(usuario);
                    return usuarioRepository.save(usuario);
                })
                .orElseGet(() -> {
                    Usuario usuario = new Usuario();
                    usuario.setNome(nome.trim());
                    usuario.setEmail(emailNormalizado);
                    usuario.setSenha(passwordEncoder.encode(UUID.randomUUID().toString()));
                    usuario.setAtivo(true);
                    usuario.setRole(roleParaEmail(emailNormalizado));
                    usuario.setOnboardingConcluido(false);
                    return usuarioRepository.save(usuario);
                });
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarAtivoPorEmail(String email) {
        String emailNormalizado = normalizarEmail(email);

        return usuarioRepository.findByEmail(emailNormalizado)
                .filter(usuario -> Boolean.TRUE.equals(usuario.getAtivo()));
    }

    @Transactional(readOnly = true)
    public Usuario autenticar(String email, String senha) {
        String emailNormalizado = normalizarEmail(email);

        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new BadCredentialsException("Email ou senha invalidos"));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new BadCredentialsException("Usuario inativo");
        }

        garantirAdminConfigurado(usuario);

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new BadCredentialsException("Email ou senha invalidos");
        }

        return usuario;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAllByOrderByDataCriacaoDesc();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado"));
    }

    public Usuario sincronizarRoleConfigurada(Usuario usuario) {
        garantirAdminConfigurado(usuario);
        return buscarPorId(usuario.getId());
    }

    public Usuario concluirOnboarding(Usuario usuarioAutenticado) {
        Usuario usuario = buscarPorId(usuarioAutenticado.getId());
        usuario.setOnboardingConcluido(true);
        usuario.setDataOnboardingConcluido(LocalDateTime.now());
        return usuarioRepository.save(usuario);
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

    public void alterarSenha(Usuario usuario, String novaSenha) {
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }

    public void deletar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    public Usuario ativar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }

    public Usuario desativar(Long id, Usuario adminAutenticado) {
        validarNaoEhProprioUsuario(id, adminAutenticado);

        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(false);
        return usuarioRepository.save(usuario);
    }

    public Usuario alterarRole(Long id, RoleUsuario role, Usuario adminAutenticado) {
        validarNaoEhProprioUsuario(id, adminAutenticado);

        Usuario usuario = buscarPorId(id);
        usuario.setRole(role);
        return usuarioRepository.save(usuario);
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }

    private RoleUsuario roleParaEmail(String email) {
        return adminEmails.contains(email) ? RoleUsuario.ADMIN : RoleUsuario.USER;
    }

    private void garantirAdminConfigurado(Usuario usuario) {
        RoleUsuario roleEsperada = roleParaEmail(usuario.getEmail());

        if (usuario.getRole() == null || (roleEsperada == RoleUsuario.ADMIN && usuario.getRole() != RoleUsuario.ADMIN)) {
            usuario.setRole(roleEsperada);
            usuarioRepository.save(usuario);
        }
    }

    private void validarNaoEhProprioUsuario(Long id, Usuario adminAutenticado) {
        if (adminAutenticado != null && id.equals(adminAutenticado.getId())) {
            throw new DomainException("Nao e permitido alterar seu proprio acesso administrativo");
        }
    }
}


