package com.ridelink.payment.service;

import com.ridelink.payment.dto.FareEstimateRequest;
import com.ridelink.payment.dto.FareEstimateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class FareServiceTest {

    private FareService fareService;

    @BeforeEach
    void setUp() {
        fareService = new FareService();
        ReflectionTestUtils.setField(fareService, "baseFare", 200.0);
        ReflectionTestUtils.setField(fareService, "perKmRate", 80.0);
        ReflectionTestUtils.setField(fareService, "currency", "LKR");
    }

    @Test
    @DisplayName("Should accurately calculate normal fare estimate")
    void testCalculateEstimateNormal() {
        FareEstimateRequest request = FareEstimateRequest.builder()
                .distanceKm(10.0)
                .serviceArea("Colombo")
                .peakHour(false)
                .build();

        FareEstimateResponse response = fareService.calculateEstimate(request);

        assertNotNull(response);
        assertEquals(200.0, response.getBaseFare());
        assertEquals(80.0, response.getPerKmRate());
        assertEquals(10.0, response.getDistanceKm());
        assertEquals(800.0, response.getDistanceCharge());
        assertEquals(1.0, response.getSurgeMultiplier());
        // (200 + 800) * 1.0 = 1000.0 LKR
        assertEquals(1000.0, response.getEstimatedFare());
        assertEquals("LKR", response.getCurrency());
        assertNotNull(response.getCalculationFormula());
    }

    @Test
    @DisplayName("Should accurately apply surge multiplier during peak hours")
    void testCalculateEstimatePeakHour() {
        FareEstimateRequest request = FareEstimateRequest.builder()
                .distanceKm(5.0)
                .serviceArea("Colombo")
                .peakHour(true)
                .build();

        FareEstimateResponse response = fareService.calculateEstimate(request);

        assertNotNull(response);
        assertEquals(1.25, response.getSurgeMultiplier());
        // (200 + (5 * 80)) * 1.25 = (200 + 400) * 1.25 = 750.0 LKR
        assertEquals(750.0, response.getEstimatedFare());
    }
}
