package com.ridelink.driver.dto;

import com.ridelink.driver.model.Vehicle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateDriverProfileRequest {

    @NotBlank(message = "User ID is required")
    @Schema(description = "Account Service User ID associated with this driver", example = "6ad14827495a0211f7495faf")
    private String userId;

    @NotBlank(message = "Driver License Number is required")
    @Schema(description = "Official driver license number", example = "B1234567")
    private String licenseNumber;

    @Min(value = 0, message = "Experience years must be 0 or more")
    @Schema(description = "Years of professional driving experience", example = "5")
    private int experienceYears;

    @NotBlank(message = "Service area is required (e.g. Colombo, Kandy)")
    @Schema(description = "Primary operational city/zone", example = "Colombo")
    private String serviceArea;

    @Valid
    @NotNull(message = "Vehicle details are required")
    @Schema(description = "Vehicle specifications and license details")
    private Vehicle vehicle;

    public CreateDriverProfileRequest() {
    }

    public CreateDriverProfileRequest(String userId, String licenseNumber, int experienceYears, String serviceArea, Vehicle vehicle) {
        this.userId = userId;
        this.licenseNumber = licenseNumber;
        this.experienceYears = experienceYears;
        this.serviceArea = serviceArea;
        this.vehicle = vehicle;
    }
}
