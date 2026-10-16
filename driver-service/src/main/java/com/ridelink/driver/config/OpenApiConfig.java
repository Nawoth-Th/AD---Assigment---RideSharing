package com.ridelink.driver.config;

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
    public OpenAPI driverServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RideLink Driver & Vehicle Service API")
                        .description("### Microservice 2: Driver & Vehicle Operations\n\n" +
                                "**Primary Owner**: Member 2 (IT24104248)\n\n" +
                                "**Database**: MongoDB (`ridelink_driver_db`)\n\n" +
                                "#### Key Responsibilities:\n" +
                                "- **Driver Operational Profiles**: Registers driver license, experience, and service area.\n" +
                                "- **Vehicle Specifications**: Manages car make, model, license plate, color, and passenger capacity.\n" +
                                "- **Operational Availability State**: Transitions between `AVAILABLE`, `ON_TRIP`, and `OFFLINE`.\n" +
                                "- **Simulated Geolocation**: Updates driver's simulated GPS latitude and longitude.\n" +
                                "- **Eligible Driver Queries**: Interservice endpoint for matching available drivers by service area.\n" +
                                "- **Trip Lifecycle Synchronization**: Callbacks (`start-trip`, `release`, `complete-ride`) for seamless dispatch orchestration.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Member 2 (IT24104248)")
                                .email("it24104248@my.sliit.lk")))
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
