package com.ridelink.ride.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FareEstimateRequest {
    private double distanceKm;
    private String serviceArea;

    public FareEstimateRequest() {
    }

    public FareEstimateRequest(double distanceKm, String serviceArea) {
        this.distanceKm = distanceKm;
        this.serviceArea = serviceArea;
    }
}
