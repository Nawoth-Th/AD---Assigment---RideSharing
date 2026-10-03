package com.ridelink.ride.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverDto {
    private String id;
    private String userId;
    private String licenseNumber;
    private double rating;
    private String availabilityStatus;
    private String serviceArea;
}
