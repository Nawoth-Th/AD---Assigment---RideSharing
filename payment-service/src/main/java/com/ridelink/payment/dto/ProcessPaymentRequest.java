package com.ridelink.payment.dto;

import com.ridelink.payment.model.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcessPaymentRequest {

    @NotBlank(message = "Ride ID is required")
    @Schema(description = "Associated ride booking ID", example = "6ad1539309fc1c708cbd375e")
    private String rideId;

    @NotBlank(message = "Passenger ID is required")
    @Schema(description = "Account Service user ID of the paying passenger", example = "6ad14827495a0211f7495faf")
    private String passengerId;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "1.0", message = "Amount must be at least 1.00")
    @Schema(description = "Total payable fare amount in LKR", example = "650.00")
    private Double amount;

    @Builder.Default
    @Schema(description = "Selected payment method (SIMULATED_WALLET, CREDIT_CARD, CASH)", example = "SIMULATED_WALLET")
    private PaymentMethod paymentMethod = PaymentMethod.SIMULATED_WALLET;

    @Schema(description = "Simulate payment failure scenario for testing error handling", example = "false")
    private boolean simulateFailure;

    public ProcessPaymentRequest() {
        this.paymentMethod = PaymentMethod.SIMULATED_WALLET;
    }

    public ProcessPaymentRequest(String rideId, String passengerId, Double amount, PaymentMethod paymentMethod, boolean simulateFailure) {
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.amount = amount;
        this.paymentMethod = paymentMethod != null ? paymentMethod : PaymentMethod.SIMULATED_WALLET;
        this.simulateFailure = simulateFailure;
    }
}
