package com.ridelink.account.dto;

import com.ridelink.account.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Schema(description = "Full name of the user", example = "Kasun Perera")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Unique email address for authentication", example = "kasun.passenger@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    @Schema(description = "Password (minimum 6 characters)", example = "Password123!")
    private String password;

    @NotBlank(message = "Phone number is required")
    @Schema(description = "Phone number with country code", example = "+94771234567")
    private String phone;

    @NotNull(message = "Role is required (PASSENGER, DRIVER, ADMIN)")
    @Schema(description = "Account role", example = "PASSENGER")
    private Role role;

    public RegisterRequest() {
    }

    public RegisterRequest(String name, String email, String password, String phone, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
    }
}
