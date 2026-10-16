package com.ridelink.account.dto;

import com.ridelink.account.model.AccountStatus;
import com.ridelink.account.model.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    @Builder.Default
    private String tokenType = "Bearer";
    private String userId;
    private String name;
    private String email;
    private Role role;
    private AccountStatus status;

    public AuthResponse() {
    }

    public AuthResponse(String token, String tokenType, String userId, String name, String email, Role role, AccountStatus status) {
        this.token = token;
        this.tokenType = tokenType;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = status;
    }
}
