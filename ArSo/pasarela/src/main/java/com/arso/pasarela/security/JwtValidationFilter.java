package com.arso.pasarela.security;

import com.arso.pasarela.auth.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtValidationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtValidationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/auth/")
            || path.startsWith("/oauth2/")
            || path.startsWith("/login/oauth2/")
            || path.startsWith("/error");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);
        if (token != null) {
            try {
                jwtService.validateToken(token);
            } catch (ExpiredJwtException ex) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Token expirado");
                return;
            } catch (JwtException ex) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Token invalido");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring("Bearer ".length()).trim();
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().trim().isEmpty()) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
