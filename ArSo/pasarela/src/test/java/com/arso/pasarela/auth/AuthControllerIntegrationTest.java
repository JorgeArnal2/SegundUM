package com.arso.pasarela.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    private static MockWebServer mockUsuarios;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void startMockServer() throws IOException {
        mockUsuarios = new MockWebServer();
        mockUsuarios.start();
    }

    @AfterAll
    static void stopMockServer() throws IOException {
        mockUsuarios.shutdown();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("microservicios.usuarios.base-url",
            () -> mockUsuarios.url("/api/").toString());
    }

    @Test
    void login_credencialesValidas_devuelveTokenYCookie() throws Exception {
        mockUsuarios.enqueue(new MockResponse()
            .setBody("{\"id\":\"usr-1\",\"email\":\"user@test.com\",\"nombre\":\"Ana\","
                + "\"apellidos\":\"Buyer\",\"administrador\":false,\"rol\":\"USER\"}")
            .addHeader("Content-Type", "application/json"));

        Map<String, String> payload = new HashMap<>();
        payload.put("email", "user@test.com");
        payload.put("password", "secret");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token", notNullValue()))
            .andExpect(jsonPath("$.id").value("usr-1"))
            .andExpect(jsonPath("$.nombreCompleto").value("Ana Buyer"))
            .andExpect(jsonPath("$.roles[0]").value("USUARIO"))
            .andExpect(cookie().value("jwt", notNullValue()));
    }

    @Test
    void login_credencialesInvalidas_devuelve401() throws Exception {
        mockUsuarios.enqueue(new MockResponse().setResponseCode(401).setBody("Credenciales invalidas"));

        Map<String, String> payload = new HashMap<>();
        payload.put("email", "user@test.com");
        payload.put("password", "wrong");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error", containsString("Credenciales")));
    }

    @Test
    void logout_eliminaCookieJwt() throws Exception {
        mockMvc.perform(post("/auth/logout"))
            .andExpect(status().isNoContent())
            .andExpect(cookie().maxAge("jwt", 0));
    }
}
