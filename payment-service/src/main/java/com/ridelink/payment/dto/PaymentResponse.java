package com.ridelink.payment.dto;

import com.ridelink.payment.model.PaymentMethod;
import com.ridelink.payment.model.PaymentStatus;
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
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private String receiptNumber;
    private String transactionReference;
    private String failureReason;
    private Instant processedAt;
}
