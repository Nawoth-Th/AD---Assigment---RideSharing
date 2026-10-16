package com.ridelink.ride.model;

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
@Document(collection = "rides")
public class Ride {

    @Id
    private String id;

    @Indexed
    private String passengerId;

    @Indexed
    private String driverId;

    private LocationPoint pickupLocation;

    private LocationPoint destinationLocation;

    private double estimatedDistanceKm;

    private double estimatedFare;

    private Double finalFare;

    @Builder.Default
    private RideStatus status = RideStatus.REQUESTED;

    private String paymentId;

    private String cancellationReason;

    @CreatedDate
    private Instant requestedAt;

    private Instant assignedAt;

    private Instant acceptedAt;

    private Instant startedAt;

    private Instant completedAt;

    private Instant cancelledAt;

    @LastModifiedDate
    private Instant updatedAt;

    public Ride() {
        this.status = RideStatus.REQUESTED;
    }

    public Ride(String id, String passengerId, String driverId, LocationPoint pickupLocation, LocationPoint destinationLocation, double estimatedDistanceKm, double estimatedFare, Double finalFare, RideStatus status, String paymentId, String cancellationReason, Instant requestedAt, Instant assignedAt, Instant acceptedAt, Instant startedAt, Instant completedAt, Instant cancelledAt, Instant updatedAt) {
        this.id = id;
        this.passengerId = passengerId;
        this.driverId = driverId;
        this.pickupLocation = pickupLocation;
        this.destinationLocation = destinationLocation;
        this.estimatedDistanceKm = estimatedDistanceKm;
        this.estimatedFare = estimatedFare;
        this.finalFare = finalFare;
        this.status = status != null ? status : RideStatus.REQUESTED;
        this.paymentId = paymentId;
        this.cancellationReason = cancellationReason;
        this.requestedAt = requestedAt;
        this.assignedAt = assignedAt;
        this.acceptedAt = acceptedAt;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.cancelledAt = cancelledAt;
        this.updatedAt = updatedAt;
    }
}
