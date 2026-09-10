package com.arso.pasarela.auth;

import com.arso.pasarela.adapter.retrofit.UsuarioAuthDTO;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtServiceTest {

    @Test
    void generateYValidateToken_mantieneClaims() {
        JwtService jwtService = new JwtService("test-secret", 3600);

        UsuarioAuthDTO usuario = new UsuarioAuthDTO();
        usuario.setId("usr-1");
        usuario.setEmail("user@test.com");
        usuario.setNombre("Ana");
        usuario.setAdministrador(false);

        String token = jwtService.generateToken(usuario);
        Claims claims = jwtService.validateToken(token);

        assertEquals("usr-1", claims.get("sub"));
        assertEquals("user@test.com", claims.get("email"));
        assertEquals("USER", claims.get("roles"));
        assertNotNull(claims.getExpiration());
    }
}
