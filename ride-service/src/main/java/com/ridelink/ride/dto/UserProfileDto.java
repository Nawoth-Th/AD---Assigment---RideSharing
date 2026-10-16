package com.ridelink.ride.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileDto {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private String status;

    public UserProfileDto() {
    }


    public UserProfileDto(String id, String name, String email, String phone, String role, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;
    }
}
