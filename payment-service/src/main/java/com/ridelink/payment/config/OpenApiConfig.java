package com.ridelink.payment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI paymentServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RideLink Fare & Payment Service API")
                        .description("Microservice 4: Fare Estimation, Pricing Rules, Simulated Payment Processing and Digital Receipts\n" +
                                "Owned by Member 4 (IT24610827)\n" +
                                "Database: MongoDB (ridelink_payment_db)")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Member 4 (IT24610827)")
                                .email("it24610827@my.sliit.lk")));
    }
}
