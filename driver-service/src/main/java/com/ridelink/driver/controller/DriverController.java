package com.ridelink.driver.controller;

import com.ridelink.driver.dto.CreateDriverProfileRequest;
import com.ridelink.driver.dto.DriverResponse;
import com.ridelink.driver.dto.UpdateAvailabilityRequest;
import com.ridelink.driver.dto.UpdateLocationRequest;
import com.ridelink.driver.dto.UpdateVehicleRequest;
import com.ridelink.driver.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
@Tag(name = "Driver & Vehicle Operations", description = "Endpoints for driver profiles, vehicle registration, availability, and eligible driver queries (Owned by Member 2 - IT24104248)")
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    @Operation(summary = "Create driver operational profile", description = "Registers vehicle and operational details for an approved driver account")
    public ResponseEntity<DriverResponse> createProfile(@Valid @RequestBody CreateDriverProfileRequest request) {
        DriverResponse response = driverService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get driver profile by ID", description = "Retrieves driver details, vehicle information, and availability status")
    public ResponseEntity<DriverResponse> getProfileById(@PathVariable String id) {
        return ResponseEntity.ok(driverService.getProfileById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get driver profile by User ID", description = "Retrieves operational profile linked to an account service User ID")
    public ResponseEntity<DriverResponse> getProfileByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(driverService.getProfileByUserId(userId));
    }

    @GetMapping
    @Operation(summary = "List all drivers", description = "Retrieves all registered driver profiles")
    public ResponseEntity<List<DriverResponse>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    @PatchMapping("/{id}/availability")
    @Operation(summary = "Update availability status", description = "Allows driver or system to toggle state between AVAILABLE, ON_TRIP, and OFFLINE")
    public ResponseEntity<DriverResponse> updateAvailability(
            @PathVariable String id,
            @Valid @RequestBody UpdateAvailabilityRequest request) {
        return ResponseEntity.ok(driverService.updateAvailability(id, request));
    }

    @PatchMapping("/{id}/location")
    @Operation(summary = "Update simulated GPS location", description = "Simulates driver's current coordinates and physical address")
    public ResponseEntity<DriverResponse> updateLocation(
            @PathVariable String id,
            @Valid @RequestBody UpdateLocationRequest request) {
        return ResponseEntity.ok(driverService.updateLocation(id, request));
    }

    @PutMapping("/{id}/vehicle")
    @Operation(summary = "Update vehicle details", description = "Updates car make, model, license plate, color, and capacity")
    public ResponseEntity<DriverResponse> updateVehicle(
            @PathVariable String id,
            @Valid @RequestBody UpdateVehicleRequest request) {
        return ResponseEntity.ok(driverService.updateVehicle(id, request));
    }

    @GetMapping("/eligible")
    @Operation(summary = "Retrieve eligible available drivers", description = "Core interservice endpoint called by Ride Management Service to locate available drivers by area/coordinates")
    public ResponseEntity<List<DriverResponse>> getEligibleDrivers(
            @RequestParam(required = false) String serviceArea,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude) {
        return ResponseEntity.ok(driverService.findEligibleAvailableDrivers(serviceArea, latitude, longitude));
    }

    @PostMapping("/{id}/complete-ride")
    @Operation(summary = "Notify driver ride completion", description = "Interservice callback to reset driver status to AVAILABLE and increment total rides")
    public ResponseEntity<Void> completeRide(@PathVariable String id) {
        driverService.incrementRidesCompleted(id);
        return ResponseEntity.noContent().build();
    }
}
