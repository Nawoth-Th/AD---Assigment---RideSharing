package com.ridelink.payment.dto;

import com.ridelink.payment.model.PaymentMethod;
import com.ridelink.payment.model.PaymentStatus;
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
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private String receiptNumber;
    private String transactionReference;
    private String failureReason;
    private Instant processedAt;

    public PaymentResponse() {
    }

    public PaymentResponse(String id, String rideId, String passengerId, double amount, String currency, PaymentStatus status, PaymentMethod paymentMethod, String receiptNumber, String transactionReference, String failureReason, Instant processedAt) {
        this.id = id;
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.receiptNumber = receiptNumber;
        this.transactionReference = transactionReference;
        this.failureReason = failureReason;
        this.processedAt = processedAt;
    }
}
