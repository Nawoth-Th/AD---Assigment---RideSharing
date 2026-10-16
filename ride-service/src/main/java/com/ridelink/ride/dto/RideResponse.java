package com.ridelink.ride.dto;

import com.ridelink.ride.model.LocationPoint;
import com.ridelink.ride.model.RideStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class RideResponse {
    private String id;
    private String passengerId;
    private String driverId;
    private LocationPoint pickupLocation;
    private LocationPoint destinationLocation;
    private double estimatedDistanceKm;
    private double estimatedFare;
    private Double finalFare;
    private RideStatus status;
    private String paymentId;
    private String cancellationReason;
    private Instant requestedAt;
    private Instant assignedAt;
    private Instant acceptedAt;
    private Instant startedAt;
    private Instant completedAt;
    private Instant cancelledAt;

    public RideResponse() {
    }

    public RideResponse(String id, String passengerId, String driverId, LocationPoint pickupLocation, LocationPoint destinationLocation, double estimatedDistanceKm, double estimatedFare, Double finalFare, RideStatus status, String paymentId, String cancellationReason, Instant requestedAt, Instant assignedAt, Instant acceptedAt, Instant startedAt, Instant completedAt, Instant cancelledAt) {
        this.id = id;
        this.passengerId = passengerId;
        this.driverId = driverId;
        this.pickupLocation = pickupLocation;
        this.destinationLocation = destinationLocation;
        this.estimatedDistanceKm = estimatedDistanceKm;
        this.estimatedFare = estimatedFare;
        this.finalFare = finalFare;
        this.status = status;
        this.paymentId = paymentId;
        this.cancellationReason = cancellationReason;
        this.requestedAt = requestedAt;
        this.assignedAt = assignedAt;
        this.acceptedAt = acceptedAt;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.cancelledAt = cancelledAt;
    }
}
