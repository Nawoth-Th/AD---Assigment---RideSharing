package com.ridelink.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FareEstimateResponse {
    private double baseFare;
    private double perKmRate;
    private double distanceKm;
    private double surgeMultiplier;
    private double distanceCharge;
    private double estimatedFare;
    private String currency;
    private String calculationFormula;
}
