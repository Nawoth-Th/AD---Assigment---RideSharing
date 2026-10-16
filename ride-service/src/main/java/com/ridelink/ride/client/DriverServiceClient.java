package com.ridelink.ride.client;

import com.ridelink.ride.dto.DriverDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
public class DriverServiceClient {

    private final RestClient driverRestClient;

    public DriverServiceClient(@Qualifier("driverRestClient") RestClient driverRestClient) {
        this.driverRestClient = driverRestClient;
    }

    public List<DriverDto> getEligibleDrivers(String serviceArea, Double lat, Double lng) {
        try {
            log.info("Interservice Call -> Driver Service: GET /api/v1/drivers/eligible?serviceArea={}&latitude={}&longitude={}",
                    serviceArea, lat, lng);
            return driverRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/drivers/eligible")
                            .queryParam("serviceArea", serviceArea != null ? serviceArea : "")
                            .queryParam("latitude", lat != null ? lat : 0.0)
                            .queryParam("longitude", lng != null ? lng : 0.0)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<DriverDto>>() {});
        } catch (Exception ex) {
            log.warn("Failed to contact Driver Service or no eligible drivers returned: {}", ex.getMessage());
            return List.of();
        }
    }

    public void notifyDriverTripStarted(String driverId) {
        try {
            log.info("Interservice Call -> Driver Service: POST /api/v1/drivers/{}/start-trip", driverId);
            driverRestClient.post()
                    .uri("/api/v1/drivers/{id}/start-trip", driverId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ex) {
            log.warn("Could not notify driver service of trip start for driver {}: {}", driverId, ex.getMessage());
        }
    }

    public void notifyDriverReleased(String driverId) {
        try {
            log.info("Interservice Call -> Driver Service: POST /api/v1/drivers/{}/release", driverId);
            driverRestClient.post()
                    .uri("/api/v1/drivers/{id}/release", driverId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ex) {
            log.warn("Could not notify driver service to release driver {}: {}", driverId, ex.getMessage());
        }
    }

    public void notifyDriverRideCompleted(String driverId) {
        try {
            log.info("Interservice Call -> Driver Service: POST /api/v1/drivers/{}/complete-ride", driverId);
            driverRestClient.post()
                    .uri("/api/v1/drivers/{id}/complete-ride", driverId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ex) {
            log.warn("Could not notify driver service of ride completion for driver {}: {}", driverId, ex.getMessage());
        }
    }
}
