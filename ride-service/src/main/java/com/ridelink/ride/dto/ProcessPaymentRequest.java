package com.ridelink.ride.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcessPaymentRequest {
    private String rideId;
    private String passengerId;
    private double amount;
    private String paymentMethod;

    public ProcessPaymentRequest() {
    }

    public ProcessPaymentRequest(String rideId, String passengerId, double amount, String paymentMethod) {
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }
}
