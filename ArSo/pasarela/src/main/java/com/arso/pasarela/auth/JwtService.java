package com.arso.pasarela.auth;

import com.arso.pasarela.adapter.retrofit.UsuarioAuthDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    private final String secret;
    private final long expirationSeconds;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationSeconds) {
        this.secret = secret;
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(UsuarioAuthDTO usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", usuario.getId());
        claims.put("email", usuario.getEmail());
        claims.put("nombre", usuario.getNombre());
        claims.put("roles", resolveJwtRole(usuario));

        Date expiration = Date.from(Instant.now().plusSeconds(expirationSeconds));
        return Jwts.builder()
            .setClaims(claims)
            .signWith(SignatureAlgorithm.HS256, secret)
            .setExpiration(expiration)
            .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody();
    }

    public List<String> resolveResponseRoles(UsuarioAuthDTO usuario) {
        if (usuario.isAdministrador() || "ADMIN".equalsIgnoreCase(usuario.getRol())
                || "ADMINISTRADOR".equalsIgnoreCase(usuario.getRol())) {
            return Collections.singletonList("ADMINISTRADOR");
        }
        return Collections.singletonList("USUARIO");
    }

    public int getExpirationSeconds() {
        return (int) expirationSeconds;
    }

    private String resolveJwtRole(UsuarioAuthDTO usuario) {
        if (usuario.getRol() != null && !usuario.getRol().trim().isEmpty()) {
            return usuario.getRol();
        }
        return usuario.isAdministrador() ? "ADMIN" : "USER";
    }
}
