package com.arso.pasarela.auth;

import com.arso.pasarela.auth.dto.AuthResponseDTO;
import com.arso.pasarela.auth.dto.LoginRequestDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final CookieTokenHelper cookieTokenHelper;

    public AuthController(AuthService authService, JwtService jwtService, CookieTokenHelper cookieTokenHelper) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.cookieTokenHelper = cookieTokenHelper;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request,
                                                 HttpServletResponse response) {
        AuthResponseDTO authResponse = authService.login(request.getEmail(), request.getPassword());
        cookieTokenHelper.setJwtCookie(response, authResponse.getToken(), jwtService.getExpirationSeconds());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        cookieTokenHelper.clearJwtCookie(response);
        return ResponseEntity.noContent().build();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        e.printStackTrace(new java.io.PrintWriter(sw));
        return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal Server Error: " + e.getMessage() + "\n" + sw.toString());
    }
}
