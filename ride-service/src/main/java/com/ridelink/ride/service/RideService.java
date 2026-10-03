package com.ridelink.ride.service;

import com.ridelink.ride.client.DriverServiceClient;
import com.ridelink.ride.client.PaymentServiceClient;
import com.ridelink.ride.dto.*;
import com.ridelink.ride.exception.InvalidRideStateException;
import com.ridelink.ride.exception.ResourceNotFoundException;
import com.ridelink.ride.model.LocationPoint;
import com.ridelink.ride.model.Ride;
import com.ridelink.ride.model.RideStatus;
import com.ridelink.ride.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final DriverServiceClient driverServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    public RideResponse requestRide(RequestRideRequest request) {
        log.info("Processing ride request for passenger: {}", request.getPassengerId());

        // 1. Calculate distance between pickup and destination
        double distanceKm = calculateDistance(request.getPickupLocation(), request.getDestinationLocation());
        if (distanceKm < 0.5) {
            distanceKm = 1.0; // Minimum 1 km for billing and simulation
        }

        // 2. Interservice Call: Request fare estimate from Fare & Payment Service
        FareEstimateResponse fareEstimate = paymentServiceClient.getFareEstimate(distanceKm, request.getServiceArea());

        // 3. Interservice Call: Query eligible drivers from Driver & Vehicle Service
        List<DriverDto> eligibleDrivers = driverServiceClient.getEligibleDrivers(
                request.getServiceArea(),
                request.getPickupLocation().getLatitude(),
                request.getPickupLocation().getLongitude());

        String assignedDriverId = null;
        RideStatus initialStatus = RideStatus.REQUESTED;
        Instant assignedAt = null;

        if (!eligibleDrivers.isEmpty()) {
            assignedDriverId = eligibleDrivers.get(0).getId();
            initialStatus = RideStatus.ASSIGNED;
            assignedAt = Instant.now();
            log.info("Auto-assigned driver {} to ride", assignedDriverId);
        } else {
            log.info("No eligible drivers currently available for area: {}", request.getServiceArea());
        }

        Ride ride = Ride.builder()
                .passengerId(request.getPassengerId())
                .driverId(assignedDriverId)
                .pickupLocation(request.getPickupLocation())
                .destinationLocation(request.getDestinationLocation())
                .estimatedDistanceKm(Math.round(distanceKm * 10.0) / 10.0)
                .estimatedFare(Math.round(fareEstimate.getEstimatedFare() * 100.0) / 100.0)
                .status(initialStatus)
                .requestedAt(Instant.now())
                .assignedAt(assignedAt)
                .updatedAt(Instant.now())
                .build();

        Ride savedRide = rideRepository.save(ride);
        return mapToResponse(savedRide);
    }

    public RideResponse assignDriver(String rideId, AssignDriverRequest request) {
        log.info("Assigning driver {} to ride {}", request.getDriverId(), rideId);
        Ride ride = getRideEntity(rideId);

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new InvalidRideStateException("Cannot assign driver. Current ride status is: " + ride.getStatus());
        }

        ride.setDriverId(request.getDriverId());
        ride.setStatus(RideStatus.ASSIGNED);
        ride.setAssignedAt(Instant.now());
        ride.setUpdatedAt(Instant.now());

        return mapToResponse(rideRepository.save(ride));
    }

    public RideResponse acceptRide(String rideId, String driverId) {
        log.info("Driver {} accepting ride {}", driverId, rideId);
        Ride ride = getRideEntity(rideId);

        if (ride.getStatus() != RideStatus.ASSIGNED) {
            throw new InvalidRideStateException("Ride is not in ASSIGNED state. Current state: " + ride.getStatus());
        }

        if (ride.getDriverId() != null && !ride.getDriverId().equals(driverId)) {
            throw new InvalidRideStateException("Ride is assigned to a different driver: " + ride.getDriverId());
        }

        ride.setStatus(RideStatus.ACCEPTED);
        ride.setAcceptedAt(Instant.now());
        ride.setUpdatedAt(Instant.now());

        return mapToResponse(rideRepository.save(ride));
    }

    public RideResponse startRide(String rideId) {
        log.info("Starting ride: {}", rideId);
        Ride ride = getRideEntity(rideId);

        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new InvalidRideStateException("Ride cannot be started. Must be in ACCEPTED state, currently: " + ride.getStatus());
        }

        ride.setStatus(RideStatus.IN_PROGRESS);
        ride.setStartedAt(Instant.now());
        ride.setUpdatedAt(Instant.now());

        return mapToResponse(rideRepository.save(ride));
    }

    public RideResponse completeRide(String rideId) {
        log.info("Completing ride: {}", rideId);
        Ride ride = getRideEntity(rideId);

        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            throw new InvalidRideStateException("Ride cannot be completed. Must be IN_PROGRESS, currently: " + ride.getStatus());
        }

        double finalFare = ride.getEstimatedFare();
        ride.setFinalFare(finalFare);
        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(Instant.now());
        ride.setUpdatedAt(Instant.now());

        // Interservice Call 1: Process payment with Fare & Payment Service
        PaymentResponse paymentResponse = paymentServiceClient.processRidePayment(
                ride.getId(), ride.getPassengerId(), finalFare);
        ride.setPaymentId(paymentResponse.getId());

        // Interservice Call 2: Notify Driver Service to restore driver status to AVAILABLE
        if (ride.getDriverId() != null) {
            driverServiceClient.notifyDriverRideCompleted(ride.getDriverId());
        }

        return mapToResponse(rideRepository.save(ride));
    }

    public RideResponse cancelRide(String rideId, CancelRideRequest request) {
        log.info("Cancelling ride: {} for reason: {}", rideId, request.getReason());
        Ride ride = getRideEntity(rideId);

        if (ride.getStatus() == RideStatus.COMPLETED) {
            throw new InvalidRideStateException("Completed rides cannot be cancelled.");
        }

        if (ride.getStatus() == RideStatus.CANCELLED) {
            throw new InvalidRideStateException("Ride is already cancelled.");
        }

        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancellationReason(request.getReason());
        ride.setCancelledAt(Instant.now());
        ride.setUpdatedAt(Instant.now());

        // Release driver if one was assigned
        if (ride.getDriverId() != null) {
            driverServiceClient.notifyDriverRideCompleted(ride.getDriverId());
        }

        return mapToResponse(rideRepository.save(ride));
    }

    public RideResponse getRideById(String id) {
        return mapToResponse(getRideEntity(id));
    }

    public List<RideResponse> getRidesByPassenger(String passengerId) {
        return rideRepository.findByPassengerId(passengerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<RideResponse> getRidesByDriver(String driverId) {
        return rideRepository.findByDriverId(driverId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<RideResponse> getAllRides() {
        return rideRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private Ride getRideEntity(String id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with ID: " + id));
    }

    private double calculateDistance(LocationPoint start, LocationPoint end) {
        if (start.getLatitude() == null || start.getLongitude() == null ||
                end.getLatitude() == null || end.getLongitude() == null) {
            return 5.0; // Default simulated 5.0 km
        }
        double dLat = Math.toRadians(end.getLatitude() - start.getLatitude());
        double dLng = Math.toRadians(end.getLongitude() - start.getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(start.getLatitude())) *
                        Math.cos(Math.toRadians(end.getLatitude())) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return 6371 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private RideResponse mapToResponse(Ride ride) {
        return RideResponse.builder()
                .id(ride.getId())
                .passengerId(ride.getPassengerId())
                .driverId(ride.getDriverId())
                .pickupLocation(ride.getPickupLocation())
                .destinationLocation(ride.getDestinationLocation())
                .estimatedDistanceKm(ride.getEstimatedDistanceKm())
                .estimatedFare(ride.getEstimatedFare())
                .finalFare(ride.getFinalFare())
                .status(ride.getStatus())
                .paymentId(ride.getPaymentId())
                .cancellationReason(ride.getCancellationReason())
                .requestedAt(ride.getRequestedAt())
                .assignedAt(ride.getAssignedAt())
                .acceptedAt(ride.getAcceptedAt())
                .startedAt(ride.getStartedAt())
                .completedAt(ride.getCompletedAt())
                .cancelledAt(ride.getCancelledAt())
                .build();
    }
}
