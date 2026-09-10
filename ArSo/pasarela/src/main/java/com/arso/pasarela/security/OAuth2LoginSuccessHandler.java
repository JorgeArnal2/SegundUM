package com.arso.pasarela.security;

import com.arso.pasarela.auth.AuthService;
import com.arso.pasarela.auth.CookieTokenHelper;
import com.arso.pasarela.auth.JwtService;
import com.arso.pasarela.auth.dto.AuthResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final JwtService jwtService;
    private final CookieTokenHelper cookieTokenHelper;
    private final ObjectMapper objectMapper;

    public OAuth2LoginSuccessHandler(AuthService authService,
                                       JwtService jwtService,
                                       CookieTokenHelper cookieTokenHelper,
                                       ObjectMapper objectMapper) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.cookieTokenHelper = cookieTokenHelper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String githubId = String.valueOf(oauthUser.getAttributes().get("id"));
        String email = oauthUser.getAttribute("email");

        AuthResponseDTO authResponse = authService.loginWithGithub(githubId, email);
        cookieTokenHelper.setJwtCookie(response, authResponse.getToken(), jwtService.getExpirationSeconds());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getWriter(), authResponse);
    }
}
