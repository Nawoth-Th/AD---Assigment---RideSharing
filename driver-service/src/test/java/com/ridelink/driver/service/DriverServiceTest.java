package com.ridelink.driver.service;

import com.ridelink.driver.dto.CreateDriverProfileRequest;
import com.ridelink.driver.dto.DriverResponse;
import com.ridelink.driver.dto.UpdateAvailabilityRequest;
import com.ridelink.driver.dto.UpdateLocationRequest;
import com.ridelink.driver.exception.DriverAlreadyExistsException;
import com.ridelink.driver.exception.ResourceNotFoundException;
import com.ridelink.driver.model.*;
import com.ridelink.driver.repository.DriverProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverProfileRepository driverProfileRepository;

    @InjectMocks
    private DriverService driverService;

    private DriverProfile sampleProfile;
    private Vehicle sampleVehicle;

    @BeforeEach
    void setUp() {
        sampleVehicle = Vehicle.builder()
                .make("Toyota")
                .model("Prius")
                .year(2022)
                .licensePlate("WP-CAD-1234")
                .color("Pearl White")
                .vehicleType(VehicleType.SEDAN)
                .passengerCapacity(4)
                .build();

        sampleProfile = DriverProfile.builder()
                .id("driver-100")
                .userId("user-driver-1")
                .licenseNumber("DL-987654321")
                .experienceYears(5)
                .rating(4.9)
                .totalRides(120)
                .availabilityStatus(AvailabilityStatus.AVAILABLE)
                .serviceArea("Colombo")
                .vehicle(sampleVehicle)
                .currentLocation(Location.builder().latitude(6.9271).longitude(79.8612).address("Colombo 03").build())
                .build();
    }

    @Test
    @DisplayName("Should successfully create driver operational profile")
    void testCreateProfileSuccess() {
        CreateDriverProfileRequest request = CreateDriverProfileRequest.builder()
                .userId("user-driver-1")
                .licenseNumber("DL-987654321")
                .experienceYears(5)
                .serviceArea("Colombo")
                .vehicle(sampleVehicle)
                .build();

        when(driverProfileRepository.existsByUserId("user-driver-1")).thenReturn(false);
        when(driverProfileRepository.existsByLicenseNumber("DL-987654321")).thenReturn(false);
        when(driverProfileRepository.save(any(DriverProfile.class))).thenReturn(sampleProfile);

        DriverResponse response = driverService.createProfile(request);

        assertNotNull(response);
        assertEquals("driver-100", response.getId());
        assertEquals("DL-987654321", response.getLicenseNumber());
        assertEquals(AvailabilityStatus.AVAILABLE, response.getAvailabilityStatus());
        verify(driverProfileRepository, times(1)).save(any(DriverProfile.class));
    }

    @Test
    @DisplayName("Should throw DriverAlreadyExistsException when user already has a driver profile")
    void testCreateProfileDuplicateUser() {
        CreateDriverProfileRequest request = CreateDriverProfileRequest.builder()
                .userId("user-driver-1")
                .licenseNumber("DL-987654321")
                .experienceYears(5)
                .serviceArea("Colombo")
                .vehicle(sampleVehicle)
                .build();

        when(driverProfileRepository.existsByUserId("user-driver-1")).thenReturn(true);

        assertThrows(DriverAlreadyExistsException.class, () -> driverService.createProfile(request));
        verify(driverProfileRepository, never()).save(any(DriverProfile.class));
    }

    @Test
    @DisplayName("Should retrieve driver profile by ID")
    void testGetProfileByIdSuccess() {
        when(driverProfileRepository.findById("driver-100")).thenReturn(Optional.of(sampleProfile));

        DriverResponse response = driverService.getProfileById("driver-100");

        assertNotNull(response);
        assertEquals("driver-100", response.getId());
        assertEquals("user-driver-1", response.getUserId());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for non-existent driver")
    void testGetProfileByIdNotFound() {
        when(driverProfileRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> driverService.getProfileById("unknown"));
    }

    @Test
    @DisplayName("Should update driver availability status")
    void testUpdateAvailability() {
        when(driverProfileRepository.findById("driver-100")).thenReturn(Optional.of(sampleProfile));
        when(driverProfileRepository.save(any(DriverProfile.class))).thenAnswer(i -> i.getArgument(0));

        UpdateAvailabilityRequest request = new UpdateAvailabilityRequest(AvailabilityStatus.ON_TRIP);
        DriverResponse response = driverService.updateAvailability("driver-100", request);

        assertEquals(AvailabilityStatus.ON_TRIP, response.getAvailabilityStatus());
    }

    @Test
    @DisplayName("Should update simulated GPS location")
    void testUpdateLocation() {
        when(driverProfileRepository.findById("driver-100")).thenReturn(Optional.of(sampleProfile));
        when(driverProfileRepository.save(any(DriverProfile.class))).thenAnswer(i -> i.getArgument(0));

        UpdateLocationRequest request = new UpdateLocationRequest(6.9000, 79.8500, "Bambalapitiya");
        DriverResponse response = driverService.updateLocation("driver-100", request);

        assertNotNull(response.getCurrentLocation());
        assertEquals(6.9000, response.getCurrentLocation().getLatitude());
        assertEquals("Bambalapitiya", response.getCurrentLocation().getAddress());
    }

    @Test
    @DisplayName("Should find eligible available drivers in service area")
    void testFindEligibleDrivers() {
        when(driverProfileRepository.findByAvailabilityStatusAndServiceAreaIgnoreCase(
                AvailabilityStatus.AVAILABLE, "Colombo")).thenReturn(List.of(sampleProfile));

        List<DriverResponse> eligible = driverService.findEligibleAvailableDrivers("Colombo", 6.9271, 79.8612);

        assertFalse(eligible.isEmpty());
        assertEquals(1, eligible.size());
        assertEquals("driver-100", eligible.get(0).getId());
    }

    @Test
    @DisplayName("Should throw DriverAlreadyExistsException when license number is already registered")
    void testCreateProfileDuplicateLicense() {
        CreateDriverProfileRequest request = CreateDriverProfileRequest.builder()
                .userId("new-user")
                .licenseNumber("DL-987654321")
                .experienceYears(3)
                .serviceArea("Colombo")
                .vehicle(sampleVehicle)
                .build();

        when(driverProfileRepository.existsByUserId("new-user")).thenReturn(false);
        when(driverProfileRepository.existsByLicenseNumber("DL-987654321")).thenReturn(true);

        assertThrows(DriverAlreadyExistsException.class, () -> driverService.createProfile(request));
        verify(driverProfileRepository, never()).save(any(DriverProfile.class));
    }

    @Test
    @DisplayName("Should get driver profile by user ID")
    void testGetProfileByUserId() {
        when(driverProfileRepository.findByUserId("user-driver-1")).thenReturn(Optional.of(sampleProfile));

        DriverResponse response = driverService.getProfileByUserId("user-driver-1");

        assertNotNull(response);
        assertEquals("driver-100", response.getId());
        assertEquals("user-driver-1", response.getUserId());
    }

    @Test
    @DisplayName("Should update vehicle details")
    void testUpdateVehicle() {
        when(driverProfileRepository.findById("driver-100")).thenReturn(Optional.of(sampleProfile));
        when(driverProfileRepository.save(any(DriverProfile.class))).thenAnswer(i -> i.getArgument(0));

        Vehicle newVehicle = Vehicle.builder()
                .make("Honda")
                .model("Civic")
                .year(2023)
                .licensePlate("WP-CAD-9999")
                .color("Black")
                .vehicleType(VehicleType.SEDAN)
                .passengerCapacity(4)
                .build();

        com.ridelink.driver.dto.UpdateVehicleRequest request = new com.ridelink.driver.dto.UpdateVehicleRequest(newVehicle);
        DriverResponse response = driverService.updateVehicle("driver-100", request);

        assertNotNull(response);
        assertEquals("WP-CAD-9999", response.getVehicle().getLicensePlate());
        assertEquals("Honda", response.getVehicle().getMake());
    }

    @Test
    @DisplayName("Should set driver status to ON_TRIP on trip start")
    void testStartTrip() {
        when(driverProfileRepository.findById("driver-100")).thenReturn(Optional.of(sampleProfile));
        when(driverProfileRepository.save(any(DriverProfile.class))).thenAnswer(i -> i.getArgument(0));

        driverService.startTrip("driver-100");

        assertEquals(AvailabilityStatus.ON_TRIP, sampleProfile.getAvailabilityStatus());
        verify(driverProfileRepository, times(1)).save(sampleProfile);
    }

    @Test
    @DisplayName("Should release driver back to AVAILABLE without incrementing total rides")
    void testReleaseDriver() {
        sampleProfile.setAvailabilityStatus(AvailabilityStatus.ON_TRIP);
        int initialRides = sampleProfile.getTotalRides();
        when(driverProfileRepository.findById("driver-100")).thenReturn(Optional.of(sampleProfile));
        when(driverProfileRepository.save(any(DriverProfile.class))).thenAnswer(i -> i.getArgument(0));

        driverService.releaseDriver("driver-100");

        assertEquals(AvailabilityStatus.AVAILABLE, sampleProfile.getAvailabilityStatus());
        assertEquals(initialRides, sampleProfile.getTotalRides());
        verify(driverProfileRepository, times(1)).save(sampleProfile);
    }

    @Test
    @DisplayName("Should increment rides completed and set status to AVAILABLE")
    void testIncrementRidesCompleted() {
        sampleProfile.setAvailabilityStatus(AvailabilityStatus.ON_TRIP);
        int initialRides = sampleProfile.getTotalRides();
        when(driverProfileRepository.findById("driver-100")).thenReturn(Optional.of(sampleProfile));
        when(driverProfileRepository.save(any(DriverProfile.class))).thenAnswer(i -> i.getArgument(0));

        driverService.incrementRidesCompleted("driver-100");

        assertEquals(AvailabilityStatus.AVAILABLE, sampleProfile.getAvailabilityStatus());
        assertEquals(initialRides + 1, sampleProfile.getTotalRides());
        verify(driverProfileRepository, times(1)).save(sampleProfile);
    }
}
