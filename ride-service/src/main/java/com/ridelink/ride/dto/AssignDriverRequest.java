package com.ridelink.ride.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssignDriverRequest {

    @NotBlank(message = "Driver ID is required")
    @Schema(description = "Driver profile ID to assign to this ride", example = "6ad1507e78c8f117e85e95e0")
    private String driverId;

    public AssignDriverRequest() {
    }

    public AssignDriverRequest(String driverId) {
        this.driverId = driverId;
    }
}
