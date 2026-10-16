package com.ridelink.account.dto;

import com.ridelink.account.model.AccountStatus;
import com.ridelink.account.model.Role;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UserProfileResponse {
    private String id;
    private String name;
    private String email;
    private String phone;
    private Role role;
    private AccountStatus status;
    private Instant createdAt;

    public UserProfileResponse() {
    }

    public UserProfileResponse(String id, String name, String email, String phone, Role role, AccountStatus status, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }
}
