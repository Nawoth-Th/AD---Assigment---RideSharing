package com.ridelink.ride.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${app.services.driver-service-url:http://localhost:8082}")
    private String driverServiceUrl;

    @Value("${app.services.payment-service-url:http://localhost:8084}")
    private String paymentServiceUrl;

    @Value("${app.services.account-service-url:http://localhost:8081}")
    private String accountServiceUrl;

    @Bean
    public RestClient driverRestClient() {
        return RestClient.builder()
                .baseUrl(driverServiceUrl)
                .build();
    }

    @Bean
    public RestClient paymentRestClient() {
        return RestClient.builder()
                .baseUrl(paymentServiceUrl)
                .build();
    }

    @Bean
    public RestClient accountRestClient() {
        return RestClient.builder()
                .baseUrl(accountServiceUrl)
                .build();
    }
}
