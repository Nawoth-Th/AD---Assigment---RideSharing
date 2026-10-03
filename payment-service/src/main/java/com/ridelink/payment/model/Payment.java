package com.ridelink.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
