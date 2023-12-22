package com.teamchallenge.bookti.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "AdBazaar Backend API Documentation",
                description = "AdBazaar Backend endpoints description",
                version = "1.0",
                contact = @Contact(
                        name = "MinoUni",
                        url = "https://github.com/MinoUni"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local dev ENV"),
                @Server(url = "https://bookti-spring-backend.onrender.com", description = "Deploy dev ENV")
        },
        security = {@SecurityRequirement(name = "bearerAuth")}
)
@SecuritySchemes(
        value = {
                @SecurityScheme(name = "bearerAuth", description = "JWT auth description", scheme = "bearer",
                        type = SecuritySchemeType.HTTP, bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
        }
)
public class SwaggerConfig {}
