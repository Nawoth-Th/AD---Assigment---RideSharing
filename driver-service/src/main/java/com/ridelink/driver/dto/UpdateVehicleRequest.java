package com.ridelink.driver.dto;

import com.ridelink.driver.model.Vehicle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateVehicleRequest {

    @Valid
    @NotNull(message = "Vehicle details are required")
    @Schema(description = "Updated vehicle technical specifications")
    private Vehicle vehicle;

    public UpdateVehicleRequest() {
    }

    public UpdateVehicleRequest(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
