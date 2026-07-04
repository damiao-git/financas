package com.house.financas.controller;

import com.house.financas.config.JwtService;
import com.house.financas.dto.LoginRequest;
import com.house.financas.dto.LoginResponse;
import com.house.financas.dto.RegisterRequest;
import com.house.financas.dto.UsuarioResponse;
import com.house.financas.model.Usuario;
import com.house.financas.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> register(@RequestBody @Valid RegisterRequest request) {
        Usuario usuario = usuarioService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.from(usuario));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        Usuario usuario = usuarioService.autenticar(
                request.getEmail(),
                request.getSenha()
        );

        String token = jwtService.gerarToken(usuario);
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
