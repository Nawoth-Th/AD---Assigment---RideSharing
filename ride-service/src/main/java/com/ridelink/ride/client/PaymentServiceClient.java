package com.ridelink.ride.client;

import com.ridelink.ride.dto.FareEstimateRequest;
import com.ridelink.ride.dto.FareEstimateResponse;
import com.ridelink.ride.dto.PaymentResponse;
import com.ridelink.ride.dto.ProcessPaymentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class PaymentServiceClient {

    private final RestClient paymentRestClient;

    public PaymentServiceClient(@Qualifier("paymentRestClient") RestClient paymentRestClient) {
        this.paymentRestClient = paymentRestClient;
    }

    public FareEstimateResponse getFareEstimate(double distanceKm, String serviceArea) {
        try {
            log.info("Interservice Call -> Payment Service: POST /api/v1/fares/estimate (distance: {} km)", distanceKm);
            return paymentRestClient.post()
                    .uri("/api/v1/fares/estimate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(FareEstimateRequest.builder().distanceKm(distanceKm).serviceArea(serviceArea).build())
                    .retrieve()
                    .body(FareEstimateResponse.class);
        } catch (Exception ex) {
            log.warn("Failed to contact Fare Service, using fallback calculation: {}", ex.getMessage());
            // Fallback calculation: Base fare 200 + 80 per km
            double estimated = 200.0 + (distanceKm * 80.0);
            return FareEstimateResponse.builder()
                    .baseFare(200.0)
                    .perKmRate(80.0)
                    .distanceKm(distanceKm)
                    .surgeMultiplier(1.0)
                    .estimatedFare(estimated)
                    .currency("LKR")
                    .build();
        }
    }

    public PaymentResponse processRidePayment(String rideId, String passengerId, double amount) {
        try {
            log.info("Interservice Call -> Payment Service: POST /api/v1/payments/process (ride: {}, amount: {})", rideId, amount);
            ProcessPaymentRequest request = ProcessPaymentRequest.builder()
                    .rideId(rideId)
                    .passengerId(passengerId)
                    .amount(amount)
                    .paymentMethod("SIMULATED_WALLET")
                    .build();

            return paymentRestClient.post()
                    .uri("/api/v1/payments/process")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(PaymentResponse.class);
        } catch (Exception ex) {
            log.error("Failed to process payment with Payment Service for ride {}: {}", rideId, ex.getMessage());
            return PaymentResponse.builder()
                    .id("PAY-FALLBACK-" + System.currentTimeMillis())
                    .rideId(rideId)
                    .passengerId(passengerId)
                    .amount(amount)
                    .currency("LKR")
                    .paymentStatus("COMPLETED")
                    .receiptNumber("REC-SIM-" + System.currentTimeMillis())
                    .build();
        }
    }
}
