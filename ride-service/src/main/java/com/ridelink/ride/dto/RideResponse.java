package com.ridelink.ride.dto;

import com.ridelink.ride.model.LocationPoint;
import com.ridelink.ride.model.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
