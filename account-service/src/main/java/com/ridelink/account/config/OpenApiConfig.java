package com.ridelink.account.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI accountServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RideLink Account Service API")
                        .description("Microservice 1: Account, Authentication and Access Management\n" +
                                "Owned by Member 1 (IT24102280)\n" +
                                "Database: MongoDB (ridelink_account_db)")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Member 1 (IT24102280)")
                                .email("it24102280@my.sliit.lk")));
    }
}
