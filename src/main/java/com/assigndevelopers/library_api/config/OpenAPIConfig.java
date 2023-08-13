package com.assigndevelopers.library_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(

                version = "1.0",
                title = "Library API Specs - assignDevelopers",
                description = "Open API Documentation for Library API",
                termsOfService = "By using this API, you 'AGREE & ACCEPT' our Terms of Usage",

                contact = @Contact(
                        name = "Anthony Stephen Aryee",
                        email = "devmail026@gmail.com",
                        url = "assigndevelopers.com"
                ),

                license = @License(
                        name = "assignDevelopers Library API License",
                        url = "assigndevelopers.com/license"
                )
        ),
        servers = {
                @Server(
                        description = "Local Environment - Dev Mode",
                        url = "http://localhost:8080"
                ),
                @Server(
                        description = "Production Environment - Prod Mode",
                        url = "https://assigndevelopers.com"
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
        description = "JWT Authorization Token",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenAPIConfig { }
