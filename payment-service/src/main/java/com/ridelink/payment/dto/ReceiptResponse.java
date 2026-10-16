package com.ridelink.payment.dto;

import com.ridelink.payment.model.PaymentMethod;
import com.ridelink.payment.model.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ReceiptResponse {
    private String receiptNumber;
    private String paymentId;
    private String rideId;
    private String passengerId;
    private double totalAmount;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String transactionReference;
    private Instant issuedAt;
    private String notes;

    public ReceiptResponse() {
    }

    public ReceiptResponse(String receiptNumber, String paymentId, String rideId, String passengerId, double totalAmount, String currency, PaymentMethod paymentMethod, PaymentStatus paymentStatus, String transactionReference, Instant issuedAt, String notes) {
        this.receiptNumber = receiptNumber;
        this.paymentId = paymentId;
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.transactionReference = transactionReference;
        this.issuedAt = issuedAt;
        this.notes = notes;
    }
}
