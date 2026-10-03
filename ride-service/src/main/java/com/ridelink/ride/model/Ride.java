package com.ridelink.ride.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
