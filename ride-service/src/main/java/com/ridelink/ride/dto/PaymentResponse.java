package com.ridelink.ride.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String id;
    private String rideId;
    private String passengerId;
    private double amount;
    private String currency;
    private String paymentStatus;
    private String receiptNumber;
    private Instant processedAt;
}
