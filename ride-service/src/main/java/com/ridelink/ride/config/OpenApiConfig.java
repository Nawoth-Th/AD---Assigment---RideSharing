package com.ridelink.ride.config;

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
    public OpenAPI rideServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RideLink Ride Management Service API")
                        .description("### Microservice 3: Ride Lifecycle, Request, Assignment & Dispatch\n\n" +
                                "**Primary Owner**: Member 3 (IT24104016)\n\n" +
                                "**Database**: MongoDB (`ridelink_ride_db`)\n\n" +
                                "#### Key Responsibilities:\n" +
                                "- **Ride Request Management**: Passenger submits pickup & drop-off locations, triggers real-time distance and fare estimation.\n" +
                                "- **Driver Matching & Dispatch**: Automatically locates nearest eligible driver from Driver Service (:8082) or manual assignment.\n" +
                                "- **State Lifecycle Machine**: Enforces strict state transitions (`REQUESTED` -> `ASSIGNED` -> `ACCEPTED` -> `IN_PROGRESS` -> `COMPLETED` / `CANCELLED`).\n" +
                                "- **Interservice Coordination**: Coordinates with Account Service (:8081) for passenger status, Driver Service (:8082) for availability locking/releasing, and Payment Service (:8084) for automatic post-trip fare collection.\n" +
                                "- **Ride History**: Provides history queries filtered by passenger or driver.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Member 3 (IT24104016)")
                                .email("it24104016@my.sliit.lk")))
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
