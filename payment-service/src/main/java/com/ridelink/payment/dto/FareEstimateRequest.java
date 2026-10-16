package com.ridelink.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FareEstimateRequest {

    @NotNull(message = "Distance in KM is required")
    @DecimalMin(value = "0.1", message = "Distance must be greater than 0")
    @Schema(description = "Trip distance in kilometers", example = "5.2")
    private Double distanceKm;

    @Schema(description = "Operational service area / zone", example = "Colombo")
    private String serviceArea;

    @Schema(description = "Whether peak-hour multiplier applies", example = "false")
    private boolean peakHour;

    public FareEstimateRequest() {
    }

    public FareEstimateRequest(Double distanceKm, String serviceArea, boolean peakHour) {
        this.distanceKm = distanceKm;
        this.serviceArea = serviceArea;
        this.peakHour = peakHour;
    }
}
