package com.ridelink.driver.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateLocationRequest {

    @NotNull(message = "Latitude is required")
    @Schema(description = "Simulated latitude coordinate", example = "6.9271")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @Schema(description = "Simulated longitude coordinate", example = "79.8612")
    private Double longitude;

    @Schema(description = "Human-readable address or landmark", example = "Colombo Fort Railway Station, Sri Lanka")
    private String address;

    public UpdateLocationRequest() {
    }

    public UpdateLocationRequest(Double latitude, Double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }
}
