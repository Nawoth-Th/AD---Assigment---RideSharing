package com.ridelink.ride.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI rideServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RideLink Ride Management Service API")
                        .description("Microservice 3: Ride Lifecycle, Request, Assignment and Dispatch\n" +
                                "Owned by Member 3 (IT24104016)\n" +
                                "Database: MongoDB (ridelink_ride_db)")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Member 3 (IT24104016)")
                                .email("it24104016@my.sliit.lk")));
    }
}
