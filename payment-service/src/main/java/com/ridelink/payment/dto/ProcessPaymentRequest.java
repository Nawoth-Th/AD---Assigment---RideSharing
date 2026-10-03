package com.ridelink.payment.dto;

import com.ridelink.payment.model.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPaymentRequest {

    @NotBlank(message = "Ride ID is required")
    private String rideId;

    @NotBlank(message = "Passenger ID is required")
    private String passengerId;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "1.0", message = "Amount must be at least 1.00")
    private Double amount;

    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.SIMULATED_WALLET;

    private boolean simulateFailure;
}
