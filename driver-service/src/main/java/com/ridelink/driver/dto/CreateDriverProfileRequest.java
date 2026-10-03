package com.ridelink.driver.dto;

import com.ridelink.driver.model.Vehicle;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
public class CreateDriverProfileRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Driver License Number is required")
    private String licenseNumber;

    @Min(value = 0, message = "Experience years must be 0 or more")
    private int experienceYears;

    @NotBlank(message = "Service area is required (e.g. Colombo, Kandy)")
    private String serviceArea;

    @Valid
    @NotNull(message = "Vehicle details are required")
    private Vehicle vehicle;
}
