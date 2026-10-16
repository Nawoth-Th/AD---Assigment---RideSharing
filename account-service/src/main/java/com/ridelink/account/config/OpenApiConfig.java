package com.ridelink.account.config;

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
    public OpenAPI accountServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RideLink Account Service API")
                        .description("### Microservice 1: Account, Authentication & Access Management\n\n" +
                                "**Primary Owner**: Member 1 (IT24102280)\n\n" +
                                "**Database**: MongoDB (`ridelink_account_db`)\n\n" +
                                "#### Key Responsibilities:\n" +
                                "- **Passenger & Driver Registration**: Creates accounts with BCrypt-hashed passwords.\n" +
                                "- **Login & JWT Token Issuance**: Authenticates credentials and issues stateless JWT tokens.\n" +
                                "- **Token Validation**: Validates token signature, expiration, and user account status.\n" +
                                "- **Role-Based Access Control**: Enforces `PASSENGER`, `DRIVER`, and `ADMIN` roles.\n" +
                                "- **Account Status Management**: Allows admins to set status (`ACTIVE`, `SUSPENDED`).\n\n" +
                                "#### How to Authorize in Swagger UI:\n" +
                                "1. Call `POST /api/v1/auth/register` or `POST /api/v1/auth/login` to obtain a JWT token.\n" +
                                "2. Click the green **Authorize** button at the top-right and paste the token.\n" +
                                "3. Execute secured endpoints like `GET /api/v1/users` or `PATCH /api/v1/users/{id}/status`.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Member 1 (IT24102280)")
                                .email("it24102280@my.sliit.lk")))
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
