package com.arso.compraventas.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class JwtTokenParser {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JwtTokenParser() {
    }

    public static JwtTokenData parse(String token) {
        if (token == null || token.isBlank()) {
            throw new JwtTokenException("Token vacío");
        }

        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            throw new JwtTokenException("Token malformado");
        }

        String payloadJson = decodeBase64Url(parts[1]);
        Map<String, Object> payload = readPayload(payloadJson);

        Object subValue = payload.get("sub");
        if (subValue == null || subValue.toString().isBlank()) {
            throw new JwtTokenException("Token sin subject");
        }

        Set<String> roles = parseRoles(payload.get("roles"));
        if (roles.isEmpty()) {
            throw new JwtTokenException("Token sin roles");
        }

        return new JwtTokenData(subValue.toString(), roles);
    }

    private static String decodeBase64Url(String value) {
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(value);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw new JwtTokenException("Token inválido", e);
        }
    }

    private static Map<String, Object> readPayload(String payloadJson) {
        try {
            return MAPPER.readValue(payloadJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new JwtTokenException("No se pudo leer el payload del token", e);
        }
    }

    private static Set<String> parseRoles(Object rolesValue) {
        if (rolesValue == null) {
            return Collections.emptySet();
        }

        Set<String> roles = new HashSet<>();
        if (rolesValue instanceof String) {
            String[] values = ((String) rolesValue).split(",");
            for (String value : values) {
                String normalized = JwtRoles.normalize(value);
                if (normalized != null && !normalized.isBlank()) {
                    roles.add(normalized);
                }
            }
        } else if (rolesValue instanceof Iterable) {
            for (Object role : (Iterable<?>) rolesValue) {
                String normalized = JwtRoles.normalize(String.valueOf(role));
                if (normalized != null && !normalized.isBlank()) {
                    roles.add(normalized);
                }
            }
        } else {
            String normalized = JwtRoles.normalize(String.valueOf(rolesValue));
            if (normalized != null && !normalized.isBlank()) {
                roles.add(normalized);
            }
        }

        return roles;
    }
}
