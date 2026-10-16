package com.ridelink.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateProfileRequest {

    @NotBlank(message = "Name cannot be blank")
    @Schema(description = "Updated full name of the user", example = "Kasun Perera (Updated)")
    private String name;

    @NotBlank(message = "Phone cannot be blank")
    @Schema(description = "Updated phone number", example = "+94779876543")
    private String phone;

    public UpdateProfileRequest() {
    }

    public UpdateProfileRequest(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }
}
