package com.arso.compraventas.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

public final class AuthorizationUtils {
    private AuthorizationUtils() {
    }

    public static void requireRole(Authentication authentication, String role) {
        requireAuthenticated(authentication);
        if (!hasRole(authentication, role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado");
        }
    }

    public static void requireSubject(Authentication authentication, String subject) {
        requireAuthenticated(authentication);
        if (subject == null || !subject.equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado");
        }
    }

    private static void requireAuthenticated(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
    }

    private static boolean hasRole(Authentication authentication, String role) {
        String authority = JwtRoles.toAuthority(role);
        for (GrantedAuthority granted : authentication.getAuthorities()) {
            if (granted.getAuthority().equals(authority)) {
                return true;
            }
        }
        return false;
    }
}
