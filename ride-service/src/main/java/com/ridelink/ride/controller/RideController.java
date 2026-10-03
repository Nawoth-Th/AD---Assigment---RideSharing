package com.ridelink.ride.controller;

import com.ridelink.ride.dto.AssignDriverRequest;
import com.ridelink.ride.dto.CancelRideRequest;
import com.ridelink.ride.dto.RequestRideRequest;
import com.ridelink.ride.dto.RideResponse;
import com.ridelink.ride.service.RideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rides")
@RequiredArgsConstructor
@Tag(name = "Ride Management & Dispatch", description = "Endpoints for ride requests, driver assignment, state lifecycle transitions, and ride histories (Owned by Member 3 - IT24104016)")
public class RideController {

    private final RideService rideService;

    @PostMapping("/request")
    @Operation(summary = "Request a new ride", description = "Creates a ride request, calculates distance, requests fare estimate, and auto-assigns an eligible driver if available")
    public ResponseEntity<RideResponse> requestRide(@Valid @RequestBody RequestRideRequest request) {
        RideResponse response = rideService.requestRide(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/assign")
    @Operation(summary = "Assign a driver to a requested ride", description = "Transitions ride from REQUESTED to ASSIGNED state")
    public ResponseEntity<RideResponse> assignDriver(
            @PathVariable String id,
            @Valid @RequestBody AssignDriverRequest request) {
        return ResponseEntity.ok(rideService.assignDriver(id, request));
    }

    @PostMapping("/{id}/accept")
    @Operation(summary = "Driver accepts assigned ride", description = "Transitions ride from ASSIGNED to ACCEPTED state")
    public ResponseEntity<RideResponse> acceptRide(
            @PathVariable String id,
            @RequestParam String driverId) {
        return ResponseEntity.ok(rideService.acceptRide(id, driverId));
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "Driver starts ride trip", description = "Transitions ride from ACCEPTED to IN_PROGRESS state")
    public ResponseEntity<RideResponse> startRide(@PathVariable String id) {
        return ResponseEntity.ok(rideService.startRide(id));
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Driver completes ride", description = "Transitions ride from IN_PROGRESS to COMPLETED, triggers payment processing, and notifies Driver Service")
    public ResponseEntity<RideResponse> completeRide(@PathVariable String id) {
        return ResponseEntity.ok(rideService.completeRide(id));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel ride", description = "Cancels a pending or active ride with a documented cancellation reason")
    public ResponseEntity<RideResponse> cancelRide(
            @PathVariable String id,
            @Valid @RequestBody CancelRideRequest request) {
        return ResponseEntity.ok(rideService.cancelRide(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ride by ID", description = "Retrieves complete ride details, current lifecycle state, and payment ID")
    public ResponseEntity<RideResponse> getRideById(@PathVariable String id) {
        return ResponseEntity.ok(rideService.getRideById(id));
    }

    @GetMapping("/passenger/{passengerId}")
    @Operation(summary = "Get rides by passenger ID", description = "Lists ride history for a specific passenger")
    public ResponseEntity<List<RideResponse>> getRidesByPassenger(@PathVariable String passengerId) {
        return ResponseEntity.ok(rideService.getRidesByPassenger(passengerId));
    }

    @GetMapping("/driver/{driverId}")
    @Operation(summary = "Get rides by driver ID", description = "Lists ride history for a specific driver")
    public ResponseEntity<List<RideResponse>> getRidesByDriver(@PathVariable String driverId) {
        return ResponseEntity.ok(rideService.getRidesByDriver(driverId));
    }

    @GetMapping
    @Operation(summary = "List all rides", description = "Lists all rides across the platform")
    public ResponseEntity<List<RideResponse>> getAllRides() {
        return ResponseEntity.ok(rideService.getAllRides());
    }
}
