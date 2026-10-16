package com.ridelink.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenValidationRequest {

    @NotBlank(message = "Token is required")
    @Schema(description = "JWT Token string to validate", example = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiI2YWQxNDgyNzQ5NWEwMjExZjc0OTVmYWYiLCJlbWFpbCI6Imthc3VuLnBhc3NlbmdlckBleGFtcGxlLmNvbSIsInJvbGUiOiJQQVNTRU5HRVIifQ.sample")
    private String token;

    public TokenValidationRequest() {
    }

    public TokenValidationRequest(String token) {
        this.token = token;
    }
}
