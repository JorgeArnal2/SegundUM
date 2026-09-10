package com.arso.compraventas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI compraventasOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Microservicio Compraventas")
                        .description("Registro de transacciones entre usuarios. Comunicación hexagonal con Productos y Usuarios vía Retrofit.")
                        .version("v1"));
    }
}
