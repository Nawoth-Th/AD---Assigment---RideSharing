package com.ridelink.driver.dto;

import com.ridelink.driver.model.AvailabilityStatus;
import com.ridelink.driver.model.Location;
import com.ridelink.driver.model.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse {
    private String id;
    private String userId;
    private String licenseNumber;
    private int experienceYears;
    private double rating;
    private int totalRides;
    private AvailabilityStatus availabilityStatus;
    private String serviceArea;
    private Vehicle vehicle;
    private Location currentLocation;
    private Instant createdAt;
}
