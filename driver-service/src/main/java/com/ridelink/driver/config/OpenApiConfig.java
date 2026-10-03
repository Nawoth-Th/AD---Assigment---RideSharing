package com.ridelink.driver.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI driverServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RideLink Driver & Vehicle Service API")
                        .description("Microservice 2: Driver Operational Profiles, Vehicle Management and Location/Availability\n" +
                                "Owned by Member 2 (IT24104248)\n" +
                                "Database: MongoDB (ridelink_driver_db)")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Member 2 (IT24104248)")
                                .email("it24104248@my.sliit.lk")));
    }
}
