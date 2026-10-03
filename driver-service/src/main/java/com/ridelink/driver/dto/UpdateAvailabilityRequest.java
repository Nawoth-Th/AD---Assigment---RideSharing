package com.ridelink.driver.dto;

import com.ridelink.driver.model.AvailabilityStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAvailabilityRequest {

    @NotNull(message = "Availability status is required (AVAILABLE, ON_TRIP, OFFLINE)")
    private AvailabilityStatus availabilityStatus;
}
