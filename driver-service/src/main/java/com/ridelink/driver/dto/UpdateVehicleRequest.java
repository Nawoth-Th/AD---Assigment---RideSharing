package com.ridelink.driver.dto;

import com.ridelink.driver.model.Vehicle;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVehicleRequest {

    @Valid
    @NotNull(message = "Vehicle details are required")
    private Vehicle vehicle;
}
