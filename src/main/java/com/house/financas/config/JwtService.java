package com.house.financas.config;

import com.house.financas.model.Usuario;
import com.house.financas.model.enums.RoleUsuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${monexa.jwt.secret:83Wr/IwHkVHi79LWSmgGMebVDDP2pq6h+Pj/ZC7K3wY=}")
    private String secretKey;

    @Value("${monexa.jwt.expiration-ms:7200000}")
    private long expiration;

    public String gerarToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim("id", usuario.getId())
                .claim("nome", usuario.getNome())
                .claim("role", roleDoUsuario(usuario).name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    public boolean tokenValido(String token, Usuario usuario) {
        String email = extrairEmail(token);
        return email.equals(usuario.getEmail()) && !tokenExpirado(token) && Boolean.TRUE.equals(usuario.getAtivo());
    }

    private boolean tokenExpirado(String token) {
        return extrairClaims(token).getExpiration().before(new Date());
    }

    private Claims extrairClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private RoleUsuario roleDoUsuario(Usuario usuario) {
        return usuario.getRole() == null ? RoleUsuario.USER : usuario.getRole();
    }
}
