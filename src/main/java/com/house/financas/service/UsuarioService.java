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

    private static final int MAX_TENTATIVAS_LOGIN = 5;
    private static final int MINUTOS_BLOQUEIO_LOGIN = 15;
    private static final String MENSAGEM_CREDENCIAIS_INVALIDAS = "E-mail ou senha inválidos";
    private static final String MENSAGEM_LOGIN_BLOQUEADO = "Muitas tentativas de login. Tente novamente em alguns minutos.";
    private static final String MENSAGEM_SENHA_FRACA = "A senha deve ter pelo menos 8 caracteres, com letras e números.";

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
        validarSenhaForte(senha);

        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            throw new DomainException("E-mail já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome.trim());
        usuario.setEmail(emailNormalizado);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setAtivo(true);
        usuario.setRole(roleParaEmail(emailNormalizado));
        usuario.setTentativasLoginFalhas(0);
        usuario.setTrocarSenhaNoProximoLogin(false);

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
                    usuario.setTrocarSenhaNoProximoLogin(false);
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
                    usuario.setTrocarSenhaNoProximoLogin(false);
                    return usuarioRepository.save(usuario);
                });
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarAtivoPorEmail(String email) {
        String emailNormalizado = normalizarEmail(email);

        return usuarioRepository.findByEmail(emailNormalizado)
                .filter(usuario -> Boolean.TRUE.equals(usuario.getAtivo()));
    }

    @Transactional(noRollbackFor = BadCredentialsException.class)
    public Usuario autenticar(String email, String senha) {
        String emailNormalizado = normalizarEmail(email);

        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new BadCredentialsException(MENSAGEM_CREDENCIAIS_INVALIDAS));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new BadCredentialsException("Usuário inativo");
        }

        garantirAdminConfigurado(usuario);
        validarBloqueioLogin(usuario);

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            registrarFalhaLogin(usuario);
            throw new BadCredentialsException(MENSAGEM_CREDENCIAIS_INVALIDAS);
        }

        return registrarLoginComSucesso(usuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAllByOrderByDataCriacaoDesc();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
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
                        throw new DomainException("E-mail já cadastrado");
                    });
            usuario.setEmail(emailNormalizado);
        }

        if (request.getSenha() != null && !request.getSenha().isBlank()) {
            validarSenhaForte(request.getSenha());
            usuario.setSenha(passwordEncoder.encode(request.getSenha()));
            usuario.setTentativasLoginFalhas(0);
            usuario.setBloqueadoAte(null);
            usuario.setTrocarSenhaNoProximoLogin(false);
        }

        if (request.getAtivo() != null) {
            usuario.setAtivo(request.getAtivo());
        }

        return usuarioRepository.save(usuario);
    }

    public void alterarSenha(Usuario usuario, String novaSenha) {
        validarSenhaForte(novaSenha);
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setTentativasLoginFalhas(0);
        usuario.setBloqueadoAte(null);
        usuario.setTrocarSenhaNoProximoLogin(false);
        usuarioRepository.save(usuario);
    }

    public Usuario definirSenhaTemporaria(Long id, String senhaTemporaria, Usuario adminAutenticado) {
        validarNaoEhProprioUsuario(id, adminAutenticado);
        validarSenhaForte(senhaTemporaria);

        Usuario usuario = buscarPorId(id);
        usuario.setSenha(passwordEncoder.encode(senhaTemporaria));
        usuario.setTentativasLoginFalhas(0);
        usuario.setBloqueadoAte(null);
        usuario.setTrocarSenhaNoProximoLogin(true);
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }

    public Usuario trocarSenhaObrigatoria(Usuario usuarioAutenticado, String novaSenha) {
        Usuario usuario = buscarPorId(usuarioAutenticado.getId());

        if (!Boolean.TRUE.equals(usuario.getTrocarSenhaNoProximoLogin())) {
            throw new DomainException("Não há troca de senha pendente para este usuário");
        }

        validarSenhaForte(novaSenha);
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.setTentativasLoginFalhas(0);
        usuario.setBloqueadoAte(null);
        usuario.setTrocarSenhaNoProximoLogin(false);
        return usuarioRepository.save(usuario);
    }

    public void deletar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    public Usuario ativar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(true);
        usuario.setTentativasLoginFalhas(0);
        usuario.setBloqueadoAte(null);
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
            throw new DomainException("Não é permitido alterar seu próprio acesso administrativo");
        }
    }

    private void validarSenhaForte(String senha) {
        if (senha == null || senha.length() < 8 || senha.length() > 100) {
            throw new DomainException(MENSAGEM_SENHA_FRACA);
        }

        boolean possuiLetra = senha.chars().anyMatch(Character::isLetter);
        boolean possuiNumero = senha.chars().anyMatch(Character::isDigit);

        if (!possuiLetra || !possuiNumero) {
            throw new DomainException(MENSAGEM_SENHA_FRACA);
        }
    }

    private void validarBloqueioLogin(Usuario usuario) {
        if (usuario.getBloqueadoAte() != null && usuario.getBloqueadoAte().isAfter(LocalDateTime.now())) {
            throw new BadCredentialsException(MENSAGEM_LOGIN_BLOQUEADO);
        }

        if (usuario.getBloqueadoAte() != null) {
            usuario.setBloqueadoAte(null);
            usuario.setTentativasLoginFalhas(0);
            usuarioRepository.save(usuario);
        }
    }

    private void registrarFalhaLogin(Usuario usuario) {
        int tentativas = Optional.ofNullable(usuario.getTentativasLoginFalhas()).orElse(0) + 1;
        usuario.setTentativasLoginFalhas(tentativas);

        if (tentativas >= MAX_TENTATIVAS_LOGIN) {
            usuario.setBloqueadoAte(LocalDateTime.now().plusMinutes(MINUTOS_BLOQUEIO_LOGIN));
        }

        usuarioRepository.save(usuario);
    }

    private Usuario registrarLoginComSucesso(Usuario usuario) {
        usuario.setTentativasLoginFalhas(0);
        usuario.setBloqueadoAte(null);
        usuario.setUltimoLoginEm(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }
}


