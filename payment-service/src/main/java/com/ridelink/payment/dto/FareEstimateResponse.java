package com.ridelink.payment.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FareEstimateResponse {
    private double baseFare;
    private double perKmRate;
    private double distanceKm;
    private double surgeMultiplier;
    private double distanceCharge;
    private double estimatedFare;
    private String currency;
    private String calculationFormula;

    public FareEstimateResponse() {
    }

    public FareEstimateResponse(double baseFare, double perKmRate, double distanceKm, double surgeMultiplier, double distanceCharge, double estimatedFare, String currency, String calculationFormula) {
        this.baseFare = baseFare;
        this.perKmRate = perKmRate;
        this.distanceKm = distanceKm;
        this.surgeMultiplier = surgeMultiplier;
        this.distanceCharge = distanceCharge;
        this.estimatedFare = estimatedFare;
        this.currency = currency;
        this.calculationFormula = calculationFormula;
    }
}
