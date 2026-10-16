package com.ridelink.ride.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DriverDto {
    private String id;
    private String userId;
    private String licenseNumber;
    private double rating;
    private String availabilityStatus;
    private String serviceArea;

    public DriverDto() {
    }

    public DriverDto(String id, String userId, String licenseNumber, double rating, String availabilityStatus, String serviceArea) {
        this.id = id;
        this.userId = userId;
        this.licenseNumber = licenseNumber;
        this.rating = rating;
        this.availabilityStatus = availabilityStatus;
        this.serviceArea = serviceArea;
    }
}
