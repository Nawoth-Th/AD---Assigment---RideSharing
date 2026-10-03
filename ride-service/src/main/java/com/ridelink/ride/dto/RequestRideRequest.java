package com.ridelink.ride.dto;

import com.ridelink.ride.model.LocationPoint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestRideRequest {

    @NotBlank(message = "Passenger ID is required")
    private String passengerId;

    @Valid
    @NotNull(message = "Pickup location is required")
    private LocationPoint pickupLocation;

    @Valid
    @NotNull(message = "Destination location is required")
    private LocationPoint destinationLocation;

    private String serviceArea;
}
