package com.ridelink.ride.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CancelRideRequest {

    @NotBlank(message = "Cancellation reason is required")
    @Schema(description = "Explanation for cancelling the ride", example = "Passenger schedule changed, no longer requires transport")
    private String reason;

    public CancelRideRequest() {
    }

    public CancelRideRequest(String reason) {
        this.reason = reason;
    }
}
