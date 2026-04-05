package com.techlabs.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

@Configuration
public class SwaggerConfig {

        @Bean
        OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("FortuneLife Insurance API")
                                                .version("1.0.0")
                                                .description(
                                                                "FortuneLife is a comprehensive insurance management backend built with Spring Boot 3 and Java 21. "
                                                                                +
                                                                                "It supports 4 user roles (Admin, Employee, Agent, Customer) with full RBAC, "
                                                                                +
                                                                                "insurance policy lifecycle management, Razorpay payment integration, "
                                                                                +
                                                                                "claims processing, commission tracking, dashboard analytics, and more.\n\n"
                                                                                +
                                                                                "**Authentication:** Use `/auth/login` to get a JWT token, then click the 🔒 Authorize button above and enter: `Bearer <your_token>`\n\n"
                                                                                +
                                                                                "**Quick Start:**\n" +
                                                                                "1. Register an Admin → `POST /auth/register?tempRole=ADMIN`\n"
                                                                                +
                                                                                "2. Login → `POST /auth/login` (returns `accessToken`)\n"
                                                                                +
                                                                                "3. Authorize with token → Click 🔒 above\n"
                                                                                +
                                                                                "4. Create Employee, Agent, Customer, Plans, Schemes, Policies, etc.")
                                                .contact(new Contact()
                                                                .name("FortuneLife Team")))
                                .servers(List.of(
                                                new Server().url("https://fortunelife-backend.onrender.com")
                                                                .description("Production (Render)"),
                                                new Server().url("http://localhost:8082")
                                                                .description("Local Development")))
                                .components(new Components()
                                                .addSecuritySchemes("bearer-key",
                                                                new SecurityScheme()
                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                .scheme("bearer")
                                                                                .bearerFormat("JWT")
                                                                                .description("Enter JWT token obtained from /fortuneLife/auth/login")))
                                .addSecurityItem(new SecurityRequirement().addList("bearer-key"));
        }
}
