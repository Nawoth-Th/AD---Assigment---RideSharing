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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
@Tag(name = "Driver & Vehicle Operations", description = "Endpoints for driver profiles, vehicle registration, availability, and eligible driver queries (Owned by Member 2 - IT24104248)")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    @Operation(summary = "Step 01 - Create driver operational profile", description = "Registers vehicle and operational details for an approved driver account")
    public ResponseEntity<DriverResponse> createProfile(@Valid @RequestBody CreateDriverProfileRequest request) {
        DriverResponse response = driverService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/location")
    @Operation(summary = "Step 02 - Update simulated GPS location", description = "Simulates driver's current coordinates and physical address")
    public ResponseEntity<DriverResponse> updateLocation(
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateLocationRequest request) {
        return ResponseEntity.ok(driverService.updateLocation(id, request));
    }

    @PatchMapping("/{id}/availability")
    @Operation(summary = "Step 03 - Update availability status", description = "Allows driver or system to toggle state between AVAILABLE, ON_TRIP, and OFFLINE")
    public ResponseEntity<DriverResponse> updateAvailability(
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateAvailabilityRequest request) {
        return ResponseEntity.ok(driverService.updateAvailability(id, request));
    }

    @GetMapping("/eligible")
    @Operation(summary = "Step 04 - Retrieve eligible available drivers", description = "Core interservice endpoint called by Ride Management Service to locate available drivers by area/coordinates")
    public ResponseEntity<List<DriverResponse>> getEligibleDrivers(
            @RequestParam(value = "serviceArea", required = false) String serviceArea,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude) {
        return ResponseEntity.ok(driverService.findEligibleAvailableDrivers(serviceArea, latitude, longitude));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Step 05 - Get driver profile by ID", description = "Retrieves driver details, vehicle information, and availability status")
    public ResponseEntity<DriverResponse> getProfileById(
            @PathVariable("id") String id) {
        return ResponseEntity.ok(driverService.getProfileById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Step 06 - Get driver profile by User ID", description = "Retrieves operational profile linked to an account service User ID")
    public ResponseEntity<DriverResponse> getProfileByUserId(
            @PathVariable("userId") String userId) {
        return ResponseEntity.ok(driverService.getProfileByUserId(userId));
    }

    @PutMapping("/{id}/vehicle")
    @Operation(summary = "Step 07 - Update vehicle details", description = "Updates car make, model, license plate, color, and capacity")
    public ResponseEntity<DriverResponse> updateVehicle(
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateVehicleRequest request) {
        return ResponseEntity.ok(driverService.updateVehicle(id, request));
    }

    @PostMapping("/{id}/start-trip")
    @Operation(summary = "Step 08 - Notify driver trip started", description = "Interservice callback to set driver availability to ON_TRIP")
    public ResponseEntity<Void> startTrip(@PathVariable("id") String id) {
        driverService.startTrip(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/release")
    @Operation(summary = "Step 09 - Release driver availability", description = "Interservice callback to restore driver availability to AVAILABLE without incrementing total rides (used on ride cancellation)")
    public ResponseEntity<Void> releaseDriver(@PathVariable("id") String id) {
        driverService.releaseDriver(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/complete-ride")
    @Operation(summary = "Step 10 - Notify driver ride completion", description = "Interservice callback to reset driver status to AVAILABLE and increment total rides")
    public ResponseEntity<Void> completeRide(@PathVariable("id") String id) {
        driverService.incrementRidesCompleted(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Step 11 - List all drivers", description = "Retrieves all registered driver profiles")
    public ResponseEntity<List<DriverResponse>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }
}
