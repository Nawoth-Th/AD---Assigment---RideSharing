package com.ridelink.ride.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PaymentResponse {
    private String id;
    private String rideId;
    private String passengerId;
    private double amount;
    private String currency;
    private String paymentStatus;
    private String receiptNumber;
    private Instant processedAt;

    public PaymentResponse() {
    }

    public PaymentResponse(String id, String rideId, String passengerId, double amount, String currency, String paymentStatus, String receiptNumber, Instant processedAt) {
        this.id = id;
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.amount = amount;
        this.currency = currency;
        this.paymentStatus = paymentStatus;
        this.receiptNumber = receiptNumber;
        this.processedAt = processedAt;
    }
}
