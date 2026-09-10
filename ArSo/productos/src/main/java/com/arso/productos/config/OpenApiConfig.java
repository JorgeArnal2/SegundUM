package com.arso.productos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productosOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("API Microservicio Productos")
                .description("Controlador REST de Productos con DTOs, validacion, HATEOAS y manejo global de errores")
                .version("v1"));
    }
}

