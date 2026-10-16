package com.ridelink.ride.client;

import com.ridelink.ride.dto.UserProfileDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class AccountServiceClient {

    private final RestClient accountRestClient;

    public AccountServiceClient(@Qualifier("accountRestClient") RestClient accountRestClient) {
        this.accountRestClient = accountRestClient;
    }

    public boolean isPassengerEligible(String passengerId) {
        try {
            log.info("Interservice Call -> Account Service: Checking user status for passengerId: {}", passengerId);
            UserProfileDto profile = accountRestClient.get()
                    .uri("/api/v1/users/{id}", passengerId)
                    .retrieve()
                    .body(UserProfileDto.class);

            if (profile != null && "SUSPENDED".equalsIgnoreCase(profile.getStatus())) {
                log.warn("Passenger {} is SUSPENDED. Ride request cannot proceed.", passengerId);
                return false;
            }
            return true;
        } catch (Exception ex) {
            log.warn("Account Service unavailable or passenger profile check skipped: {}", ex.getMessage());
            // Resilient microservice pattern: allow fallback so ride request does not fail if account-service is temporarily offline
            return true;
        }
    }
}
