package com.arso.compraventas.security;

import java.util.Locale;

public final class JwtRoles {
    private JwtRoles() {
    }

    public static String normalize(String role) {
        if (role == null) {
            return null;
        }
        String value = role.trim().toUpperCase(Locale.ROOT);
        if ("ADMIN".equals(value) || "ADMINISTRADOR".equals(value)) {
            return "ADMINISTRADOR";
        }
        if ("USER".equals(value) || "USUARIO".equals(value)) {
            return "USUARIO";
        }
        return value;
    }

    public static String toAuthority(String role) {
        String normalized = normalize(role);
        return normalized == null ? null : "ROLE_" + normalized;
    }
}
