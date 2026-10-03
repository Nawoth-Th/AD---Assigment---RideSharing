package com.ridelink.ride.service;

import com.ridelink.ride.client.DriverServiceClient;
import com.ridelink.ride.client.PaymentServiceClient;
import com.ridelink.ride.dto.*;
import com.ridelink.ride.exception.InvalidRideStateException;
import com.ridelink.ride.model.LocationPoint;
import com.ridelink.ride.model.Ride;
import com.ridelink.ride.model.RideStatus;
import com.ridelink.ride.repository.RideRepository;
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
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private DriverServiceClient driverServiceClient;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @InjectMocks
    private RideService rideService;

    private LocationPoint pickup;
    private LocationPoint destination;
    private Ride sampleRide;

    @BeforeEach
    void setUp() {
        pickup = LocationPoint.builder().latitude(6.9271).longitude(79.8612).address("Colombo Fort").build();
        destination = LocationPoint.builder().latitude(6.8652).longitude(79.8598).address("Dehiwala").build();

        sampleRide = Ride.builder()
                .id("ride-001")
                .passengerId("passenger-10")
                .driverId("driver-20")
                .pickupLocation(pickup)
                .destinationLocation(destination)
                .estimatedDistanceKm(7.0)
                .estimatedFare(760.0)
                .status(RideStatus.REQUESTED)
                .build();
    }

    @Test
    @DisplayName("Should create ride and auto-assign when eligible drivers exist")
    void testRequestRideWithAvailableDriver() {
        RequestRideRequest request = RequestRideRequest.builder()
                .passengerId("passenger-10")
                .pickupLocation(pickup)
                .destinationLocation(destination)
                .serviceArea("Colombo")
                .build();

        FareEstimateResponse fareResponse = FareEstimateResponse.builder()
                .estimatedFare(760.0)
                .currency("LKR")
                .build();

        DriverDto availableDriver = DriverDto.builder()
                .id("driver-20")
                .serviceArea("Colombo")
                .rating(4.8)
                .build();

        when(paymentServiceClient.getFareEstimate(anyDouble(), anyString())).thenReturn(fareResponse);
        when(driverServiceClient.getEligibleDrivers(anyString(), anyDouble(), anyDouble()))
                .thenReturn(List.of(availableDriver));
        when(rideRepository.save(any(Ride.class))).thenAnswer(i -> {
            Ride r = i.getArgument(0);
            r.setId("ride-001");
            return r;
        });

        RideResponse response = rideService.requestRide(request);

        assertNotNull(response);
        assertEquals("passenger-10", response.getPassengerId());
        assertEquals("driver-20", response.getDriverId());
        assertEquals(RideStatus.ASSIGNED, response.getStatus());
    }

    @Test
    @DisplayName("Should create ride in REQUESTED status when no drivers are available")
    void testRequestRideWithNoDriverAvailable() {
        RequestRideRequest request = RequestRideRequest.builder()
                .passengerId("passenger-10")
                .pickupLocation(pickup)
                .destinationLocation(destination)
                .serviceArea("Colombo")
                .build();

        FareEstimateResponse fareResponse = FareEstimateResponse.builder()
                .estimatedFare(760.0)
                .currency("LKR")
                .build();

        when(paymentServiceClient.getFareEstimate(anyDouble(), anyString())).thenReturn(fareResponse);
        when(driverServiceClient.getEligibleDrivers(anyString(), anyDouble(), anyDouble()))
                .thenReturn(List.of()); // No drivers
        when(rideRepository.save(any(Ride.class))).thenAnswer(i -> {
            Ride r = i.getArgument(0);
            r.setId("ride-001");
            return r;
        });

        RideResponse response = rideService.requestRide(request);

        assertNotNull(response);
        assertNull(response.getDriverId());
        assertEquals(RideStatus.REQUESTED, response.getStatus());
    }

    @Test
    @DisplayName("Should successfully transition through valid ride lifecycle")
    void testValidRideLifecycle() {
        sampleRide.setStatus(RideStatus.ASSIGNED);
        when(rideRepository.findById("ride-001")).thenReturn(Optional.of(sampleRide));
        when(rideRepository.save(any(Ride.class))).thenAnswer(i -> i.getArgument(0));

        // 1. Accept ride
        RideResponse accepted = rideService.acceptRide("ride-001", "driver-20");
        assertEquals(RideStatus.ACCEPTED, accepted.getStatus());

        // 2. Start ride
        sampleRide.setStatus(RideStatus.ACCEPTED);
        RideResponse started = rideService.startRide("ride-001");
        assertEquals(RideStatus.IN_PROGRESS, started.getStatus());

        // 3. Complete ride with payment & driver notification
        sampleRide.setStatus(RideStatus.IN_PROGRESS);
        PaymentResponse mockPayment = PaymentResponse.builder()
                .id("pay-999")
                .receiptNumber("REC-12345")
                .paymentStatus("COMPLETED")
                .build();

        when(paymentServiceClient.processRidePayment("ride-001", "passenger-10", 760.0))
                .thenReturn(mockPayment);

        RideResponse completed = rideService.completeRide("ride-001");
        assertEquals(RideStatus.COMPLETED, completed.getStatus());
        assertEquals("pay-999", completed.getPaymentId());
        verify(driverServiceClient, times(1)).notifyDriverRideCompleted("driver-20");
    }

    @Test
    @DisplayName("Should reject invalid state transition (Negative Scenario)")
    void testInvalidStateTransition() {
        sampleRide.setStatus(RideStatus.REQUESTED); // Not accepted yet
        when(rideRepository.findById("ride-001")).thenReturn(Optional.of(sampleRide));

        // Attempting to complete directly from REQUESTED must fail!
        assertThrows(InvalidRideStateException.class, () -> rideService.completeRide("ride-001"));
    }

    @Test
    @DisplayName("Should reject cancellation on already completed ride (Negative Scenario)")
    void testCannotCancelCompletedRide() {
        sampleRide.setStatus(RideStatus.COMPLETED);
        when(rideRepository.findById("ride-001")).thenReturn(Optional.of(sampleRide));

        assertThrows(InvalidRideStateException.class,
                () -> rideService.cancelRide("ride-001", new CancelRideRequest("Passenger changed mind")));
    }
}
