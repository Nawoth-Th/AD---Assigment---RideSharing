package com.ridelink.driver.dto;

import com.ridelink.driver.model.AvailabilityStatus;
import com.ridelink.driver.model.Location;
import com.ridelink.driver.model.Vehicle;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
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

    public DriverResponse() {
    }

    public DriverResponse(String id, String userId, String licenseNumber, int experienceYears, double rating, int totalRides, AvailabilityStatus availabilityStatus, String serviceArea, Vehicle vehicle, Location currentLocation, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.licenseNumber = licenseNumber;
        this.experienceYears = experienceYears;
        this.rating = rating;
        this.totalRides = totalRides;
        this.availabilityStatus = availabilityStatus;
        this.serviceArea = serviceArea;
        this.vehicle = vehicle;
        this.currentLocation = currentLocation;
        this.createdAt = createdAt;
    }
}
