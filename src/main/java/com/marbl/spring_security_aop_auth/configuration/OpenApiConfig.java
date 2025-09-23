package com.marbl.spring_security_aop_auth.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Marco Blanco",
                        email = "marcoblanco.dev@gmail.com",
                        url = "https://github.com/TenpoDev"
                ),
                description = "OpenApi documentation for MarBl spring-security-aop-auth Poc",
                title = "Spring-Security-Aop-Auth Poc - MarBl",
                version = "1.0",
                termsOfService = "This application is for a poc"
        ),
        servers = @Server(
                description = "Local ENV",
                url = "http://localhost:8080"
        )
)
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("Bearer",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ));
    }
}