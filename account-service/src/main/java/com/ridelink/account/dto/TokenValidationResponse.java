package com.ridelink.account.dto;

import com.ridelink.account.model.AccountStatus;
import com.ridelink.account.model.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenValidationResponse {
    private boolean valid;
    private String userId;
    private String email;
    private Role role;
    private AccountStatus status;
    private String message;

    public TokenValidationResponse() {
    }

    public TokenValidationResponse(boolean valid, String userId, String email, Role role, AccountStatus status, String message) {
        this.valid = valid;
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.status = status;
        this.message = message;
    }
}
