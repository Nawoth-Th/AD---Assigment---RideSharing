package com.ridelink.payment.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;

    @Indexed
    private String rideId;

    @Indexed
    private String passengerId;

    private double amount;

    @Builder.Default
    private String currency = "LKR";

    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.SIMULATED_WALLET;

    @Indexed(unique = true)
    private String receiptNumber;

    private String transactionReference;

    private String failureReason;

    private Instant processedAt;

    @CreatedDate
    private Instant createdAt;

    public Payment() {
        this.currency = "LKR";
        this.status = PaymentStatus.PENDING;
        this.paymentMethod = PaymentMethod.SIMULATED_WALLET;
    }

    public Payment(String id, String rideId, String passengerId, double amount, String currency, PaymentStatus status, PaymentMethod paymentMethod, String receiptNumber, String transactionReference, String failureReason, Instant processedAt, Instant createdAt) {
        this.id = id;
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.amount = amount;
        this.currency = currency != null ? currency : "LKR";
        this.status = status != null ? status : PaymentStatus.PENDING;
        this.paymentMethod = paymentMethod != null ? paymentMethod : PaymentMethod.SIMULATED_WALLET;
        this.receiptNumber = receiptNumber;
        this.transactionReference = transactionReference;
        this.failureReason = failureReason;
        this.processedAt = processedAt;
        this.createdAt = createdAt;
    }
}
