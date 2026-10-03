package com.ridelink.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareEstimateRequest {

    @NotNull(message = "Distance in KM is required")
    @DecimalMin(value = "0.1", message = "Distance must be greater than 0")
    private Double distanceKm;

    private String serviceArea;

    private boolean peakHour;
}
