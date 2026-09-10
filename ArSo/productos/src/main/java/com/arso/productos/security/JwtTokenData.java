package com.arso.productos.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtTokenData {
    private final String subject;
    private final Set<String> roles;

    public JwtTokenData(String subject, Set<String> roles) {
        this.subject = subject;
        this.roles = roles;
    }

    public String getSubject() {
        return subject;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public Collection<? extends GrantedAuthority> toAuthorities() {
        return roles.stream()
            .map(JwtRoles::toAuthority)
            .filter(role -> role != null && !role.isBlank())
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());
    }
}
