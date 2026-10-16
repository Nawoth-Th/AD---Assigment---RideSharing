package com.ridelink.payment.controller;

import com.ridelink.payment.dto.FareEstimateRequest;
import com.ridelink.payment.dto.FareEstimateResponse;
import com.ridelink.payment.service.FareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fares")
@Tag(name = "Fare Estimation & Calculation", description = "Endpoints for transparent ride fare calculations using base fare and distance metrics (Owned by Member 4 - IT24610827)")
public class FareController {

    private final FareService fareService;

    public FareController(FareService fareService) {
        this.fareService = fareService;
    }

    @PostMapping("/estimate")
    @Operation(summary = "Calculate ride fare estimate", description = "Calculates fare based on distance (km), base rate, and surge factor, returning itemized breakdown")
    public ResponseEntity<FareEstimateResponse> getEstimate(@Valid @RequestBody FareEstimateRequest request) {
        return ResponseEntity.ok(fareService.calculateEstimate(request));
    }
}
