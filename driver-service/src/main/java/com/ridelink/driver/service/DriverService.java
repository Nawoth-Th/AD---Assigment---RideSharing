package com.ridelink.driver.service;

import com.ridelink.driver.dto.CreateDriverProfileRequest;
import com.ridelink.driver.dto.DriverResponse;
import com.ridelink.driver.dto.UpdateAvailabilityRequest;
import com.ridelink.driver.dto.UpdateLocationRequest;
import com.ridelink.driver.dto.UpdateVehicleRequest;
import com.ridelink.driver.exception.DriverAlreadyExistsException;
import com.ridelink.driver.exception.ResourceNotFoundException;
import com.ridelink.driver.model.AvailabilityStatus;
import com.ridelink.driver.model.DriverProfile;
import com.ridelink.driver.model.Location;
import com.ridelink.driver.repository.DriverProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DriverService {

    private final DriverProfileRepository driverProfileRepository;

    public DriverService(DriverProfileRepository driverProfileRepository) {
        this.driverProfileRepository = driverProfileRepository;
    }

    public DriverResponse createProfile(CreateDriverProfileRequest request) {
        log.info("Creating driver operational profile for userId: {}", request.getUserId());

        if (driverProfileRepository.existsByUserId(request.getUserId())) {
            throw new DriverAlreadyExistsException("Driver profile already exists for user ID: " + request.getUserId());
        }

        if (driverProfileRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DriverAlreadyExistsException("License number is already registered: " + request.getLicenseNumber());
        }

        DriverProfile profile = DriverProfile.builder()
                .userId(request.getUserId())
                .licenseNumber(request.getLicenseNumber())
                .experienceYears(request.getExperienceYears())
                .rating(5.0)
                .totalRides(0)
                .availabilityStatus(AvailabilityStatus.AVAILABLE)
                .serviceArea(request.getServiceArea().trim())
                .vehicle(request.getVehicle())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        DriverProfile saved = driverProfileRepository.save(profile);
        return mapToResponse(saved);
    }

    public DriverResponse getProfileById(String id) {
        log.info("Fetching driver profile by id: {}", id);
        DriverProfile profile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found with ID: " + id));
        return mapToResponse(profile);
    }

    public DriverResponse getProfileByUserId(String userId) {
        log.info("Fetching driver profile by userId: {}", userId);
        DriverProfile profile = driverProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found for user ID: " + userId));
        return mapToResponse(profile);
    }

    public List<DriverResponse> getAllDrivers() {
        log.info("Fetching all driver profiles");
        return driverProfileRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public DriverResponse updateAvailability(String id, UpdateAvailabilityRequest request) {
        log.info("Updating availability for driver: {} to {}", id, request.getAvailabilityStatus());
        DriverProfile profile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found with ID: " + id));

        profile.setAvailabilityStatus(request.getAvailabilityStatus());
        profile.setUpdatedAt(Instant.now());

        DriverProfile updated = driverProfileRepository.save(profile);
        return mapToResponse(updated);
    }

    public DriverResponse updateLocation(String id, UpdateLocationRequest request) {
        log.info("Updating simulated location for driver: {}", id);
        DriverProfile profile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found with ID: " + id));

        Location location = Location.builder()
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(request.getAddress())
                .updatedAt(Instant.now())
                .build();

        profile.setCurrentLocation(location);
        profile.setUpdatedAt(Instant.now());

        DriverProfile updated = driverProfileRepository.save(profile);
        return mapToResponse(updated);
    }

    public DriverResponse updateVehicle(String id, UpdateVehicleRequest request) {
        log.info("Updating vehicle details for driver: {}", id);
        DriverProfile profile = driverProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver profile not found with ID: " + id));

        profile.setVehicle(request.getVehicle());
        profile.setUpdatedAt(Instant.now());

        DriverProfile updated = driverProfileRepository.save(profile);
        return mapToResponse(updated);
    }

    public List<DriverResponse> findEligibleAvailableDrivers(String serviceArea, Double lat, Double lng) {
        log.info("Searching for eligible drivers in area: {}, lat: {}, lng: {}", serviceArea, lat, lng);

        List<DriverProfile> availableDrivers;
        if (serviceArea != null && !serviceArea.isBlank()) {
            availableDrivers = driverProfileRepository.findByAvailabilityStatusAndServiceAreaIgnoreCase(
                    AvailabilityStatus.AVAILABLE, serviceArea.trim());
            // If none found in specific named zone, fall back to any available driver for maximum flexibility
            if (availableDrivers.isEmpty()) {
                availableDrivers = driverProfileRepository.findByAvailabilityStatus(AvailabilityStatus.AVAILABLE);
            }
        } else {
            availableDrivers = driverProfileRepository.findByAvailabilityStatus(AvailabilityStatus.AVAILABLE);
        }

        List<DriverProfile> resultList = new java.util.ArrayList<>(availableDrivers);
        // If coordinates provided, sort by simulated distance
        if (lat != null && lng != null) {
            resultList.sort(Comparator.comparingDouble(d -> calculateDistance(d, lat, lng)));
        }

        return resultList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void startTrip(String driverId) {
        log.info("Setting driver {} status to ON_TRIP", driverId);
        driverProfileRepository.findById(driverId).ifPresent(profile -> {
            profile.setAvailabilityStatus(AvailabilityStatus.ON_TRIP);
            profile.setUpdatedAt(Instant.now());
            driverProfileRepository.save(profile);
        });
    }

    public void releaseDriver(String driverId) {
        log.info("Releasing driver {} status back to AVAILABLE without incrementing total rides", driverId);
        driverProfileRepository.findById(driverId).ifPresent(profile -> {
            profile.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
            profile.setUpdatedAt(Instant.now());
            driverProfileRepository.save(profile);
        });
    }

    public void incrementRidesCompleted(String driverId) {
        driverProfileRepository.findById(driverId).ifPresent(profile -> {
            profile.setTotalRides(profile.getTotalRides() + 1);
            profile.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);
            profile.setUpdatedAt(Instant.now());
            driverProfileRepository.save(profile);
        });
    }

    private double calculateDistance(DriverProfile profile, double targetLat, double targetLng) {
        if (profile.getCurrentLocation() == null ||
                profile.getCurrentLocation().getLatitude() == null ||
                profile.getCurrentLocation().getLongitude() == null) {
            return Double.MAX_VALUE;
        }
        double dLat = Math.toRadians(targetLat - profile.getCurrentLocation().getLatitude());
        double dLng = Math.toRadians(targetLng - profile.getCurrentLocation().getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(profile.getCurrentLocation().getLatitude())) *
                        Math.cos(Math.toRadians(targetLat)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return 6371 * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private DriverResponse mapToResponse(DriverProfile profile) {
        return DriverResponse.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .licenseNumber(profile.getLicenseNumber())
                .experienceYears(profile.getExperienceYears())
                .rating(profile.getRating())
                .totalRides(profile.getTotalRides())
                .availabilityStatus(profile.getAvailabilityStatus())
                .serviceArea(profile.getServiceArea())
                .vehicle(profile.getVehicle())
                .currentLocation(profile.getCurrentLocation())
                .createdAt(profile.getCreatedAt())
                .build();
    }
}
