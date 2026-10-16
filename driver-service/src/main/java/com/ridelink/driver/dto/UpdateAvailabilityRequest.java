package com.ridelink.driver.dto;

import com.ridelink.driver.model.AvailabilityStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateAvailabilityRequest {

    @NotNull(message = "Availability status is required (AVAILABLE, ON_TRIP, OFFLINE)")
    @Schema(description = "Driver operational availability state", example = "AVAILABLE")
    private AvailabilityStatus availabilityStatus;

    public UpdateAvailabilityRequest() {
    }

    public UpdateAvailabilityRequest(AvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }
}
