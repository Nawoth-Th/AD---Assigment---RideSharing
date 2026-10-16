package com.ridelink.ride.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FareEstimateResponse {
    private double baseFare;
    private double perKmRate;
    private double distanceKm;
    private double surgeMultiplier;
    private double estimatedFare;
    private String currency;

    public FareEstimateResponse() {
    }

    public FareEstimateResponse(double baseFare, double perKmRate, double distanceKm, double surgeMultiplier, double estimatedFare, String currency) {
        this.baseFare = baseFare;
        this.perKmRate = perKmRate;
        this.distanceKm = distanceKm;
        this.surgeMultiplier = surgeMultiplier;
        this.estimatedFare = estimatedFare;
        this.currency = currency;
    }
}
