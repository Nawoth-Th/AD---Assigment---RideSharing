package com.ridelink.payment.service;

import com.ridelink.payment.dto.FareEstimateRequest;
import com.ridelink.payment.dto.FareEstimateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FareService {

    @Value("${app.fare.base-fare:200.0}")
    private double baseFare;

    @Value("${app.fare.per-km-rate:80.0}")
    private double perKmRate;

    @Value("${app.fare.currency:LKR}")
    private String currency;

    public FareEstimateResponse calculateEstimate(FareEstimateRequest request) {
        double distance = request.getDistanceKm();
        double surgeMultiplier = request.isPeakHour() ? 1.25 : 1.0;

        // Documented Calculation Rule:
        // Distance Charge = Distance (km) * Per-KM Rate
        // Total Estimated Fare = (Base Fare + Distance Charge) * Surge Multiplier
        double distanceCharge = distance * perKmRate;
        double totalFare = (baseFare + distanceCharge) * surgeMultiplier;
        double roundedFare = Math.round(totalFare * 100.0) / 100.0;

        log.info("Calculated fare estimate for {} km (peak: {}): {} {}",
                distance, request.isPeakHour(), roundedFare, currency);

        return FareEstimateResponse.builder()
                .baseFare(baseFare)
                .perKmRate(perKmRate)
                .distanceKm(distance)
                .distanceCharge(Math.round(distanceCharge * 100.0) / 100.0)
                .surgeMultiplier(surgeMultiplier)
                .estimatedFare(roundedFare)
                .currency(currency)
                .calculationFormula(String.format("(BaseFare[%.2f] + (%.2f km * %.2f/km)) * Surge[%.2f]",
                        baseFare, distance, perKmRate, surgeMultiplier))
                .build();
    }
}
