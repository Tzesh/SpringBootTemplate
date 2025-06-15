package com.tzesh.springtemplate.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * OpenApiConfig is a configuration class for OpenAPI
 * @author tzesh
 */
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Uğur Dindar",
                        email = "mail@ugurdindar.com",
                        url = "https://ugurdindar.com"
                ),
                description = "Production-ready Spring Boot template for secure REST APIs with JWT, PostgreSQL, Hibernate, Redis, Kafka, Zookeeper, MapStruct, Lombok, and Docker Compose. Fast, modern, and cloud-ready.",
                title = "Spring Boot Template",
                version = "2.1.0",
                license = @License(
                        name = "GitHub Repository",
                        url = "https://github.com/tzesh/SpringBootTemplate"
                )
        ),
        servers = {
                @Server(
                        description = "local environment",
                        url = "http://localhost:8080/api/v1"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
