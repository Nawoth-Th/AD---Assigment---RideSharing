package com.ridelink.ride.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationPoint {

    @NotNull(message = "Latitude is required")
    @Schema(description = "GPS latitude coordinate", example = "6.9319")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @Schema(description = "GPS longitude coordinate", example = "79.8454")
    private Double longitude;

    @NotBlank(message = "Address or place name is required")
    @Schema(description = "Physical address or landmark name", example = "Colombo Fort Railway Station, Colombo")
    private String address;

    public LocationPoint() {
    }

    public LocationPoint(Double latitude, Double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }
}
