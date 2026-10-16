package com.ridelink.driver.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document(collection = "drivers")
public class DriverProfile {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    @Indexed(unique = true)
    private String licenseNumber;

    private int experienceYears;

    @Builder.Default
    private double rating = 5.0;

    @Builder.Default
    private int totalRides = 0;

    @Builder.Default
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.OFFLINE;

    private String serviceArea;

    private Vehicle vehicle;

    private Location currentLocation;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public DriverProfile() {
        this.rating = 5.0;
        this.totalRides = 0;
        this.availabilityStatus = AvailabilityStatus.OFFLINE;
    }

    public DriverProfile(String id, String userId, String licenseNumber, int experienceYears, double rating, int totalRides, AvailabilityStatus availabilityStatus, String serviceArea, Vehicle vehicle, Location currentLocation, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.licenseNumber = licenseNumber;
        this.experienceYears = experienceYears;
        this.rating = rating;
        this.totalRides = totalRides;
        this.availabilityStatus = availabilityStatus != null ? availabilityStatus : AvailabilityStatus.OFFLINE;
        this.serviceArea = serviceArea;
        this.vehicle = vehicle;
        this.currentLocation = currentLocation;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
