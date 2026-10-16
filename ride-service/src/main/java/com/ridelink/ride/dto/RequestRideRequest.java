package com.ridelink.ride.dto;

import com.ridelink.ride.model.LocationPoint;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestRideRequest {

    @NotBlank(message = "Passenger ID is required")
    @Schema(description = "Account Service user ID of the requesting passenger", example = "6ad14827495a0211f7495faf")
    private String passengerId;

    @Valid
    @NotNull(message = "Pickup location is required")
    @Schema(description = "Starting pickup location and GPS coordinates")
    private LocationPoint pickupLocation;

    @Valid
    @NotNull(message = "Destination location is required")
    @Schema(description = "Drop-off destination and GPS coordinates")
    private LocationPoint destinationLocation;

    @Schema(description = "Operational service area / city zone", example = "Colombo")
    private String serviceArea;

    public RequestRideRequest() {
    }

    public RequestRideRequest(String passengerId, LocationPoint pickupLocation, LocationPoint destinationLocation, String serviceArea) {
        this.passengerId = passengerId;
        this.pickupLocation = pickupLocation;
        this.destinationLocation = destinationLocation;
        this.serviceArea = serviceArea;
    }
}
