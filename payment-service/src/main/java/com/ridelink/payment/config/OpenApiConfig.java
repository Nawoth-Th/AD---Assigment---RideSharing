package com.ridelink.payment.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI paymentServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RideLink Fare & Payment Service API")
                        .description("### Microservice 4: Fare Estimation & Simulated Payments\n\n" +
                                "**Primary Owner**: Member 4 (IT24610827)\n\n" +
                                "**Database**: MongoDB (`ridelink_payment_db`)\n\n" +
                                "#### Key Responsibilities:\n" +
                                "- **Fare Estimation Algorithm**: Calculates itemized pricing using base fare, distance rates, and surge multipliers.\n" +
                                "- **Simulated Payment Processing**: Processes mock transactions (`SIMULATED_WALLET`, `CREDIT_CARD`, `CASH`) with status confirmation.\n" +
                                "- **Failure Simulation Engine**: Supports simulated edge-case failures (`simulateFailure: true`) for testing negative settlement paths.\n" +
                                "- **Digital Receipt Generation**: Issues structured receipt metadata, transaction references, and timestamped audit logs.\n" +
                                "- **Interservice Integration**: Provides synchronous estimation and processing endpoints consumed by Ride Management Service (:8083).")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Member 4 (IT24610827)")
                                .email("it24610827@my.sliit.lk")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter your JWT token (without Bearer prefix). Swagger UI will automatically attach Authorization: Bearer <token>.")));
    }
}
