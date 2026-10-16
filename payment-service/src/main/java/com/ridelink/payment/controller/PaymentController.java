package com.ridelink.payment.controller;

import com.ridelink.payment.dto.PaymentResponse;
import com.ridelink.payment.dto.ProcessPaymentRequest;
import com.ridelink.payment.dto.ReceiptResponse;
import com.ridelink.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Simulated Payment & Receipts", description = "Endpoints for processing simulated ride payments and issuing digital receipts (Owned by Member 4 - IT24610827)")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


    @PostMapping("/process")
    @Operation(summary = "Process simulated payment", description = "Records a simulated transaction, generates receipt number, and confirms payment status")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody ProcessPaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieves payment status and transaction details")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable("id") String id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/ride/{rideId}")
    @Operation(summary = "Get payment for a ride", description = "Retrieves payment details by associated ride ID")
    public ResponseEntity<PaymentResponse> getPaymentByRideId(@PathVariable("rideId") String rideId) {
        return ResponseEntity.ok(paymentService.getPaymentByRideId(rideId));
    }

    @GetMapping("/receipt/{receiptNumber}")
    @Operation(summary = "Get official digital receipt", description = "Retrieves formatted receipt with payment breakdown and transaction reference")
    public ResponseEntity<ReceiptResponse> getReceipt(@PathVariable("receiptNumber") String receiptNumber) {
        return ResponseEntity.ok(paymentService.getReceiptByReceiptNumber(receiptNumber));
    }

    @GetMapping
    @Operation(summary = "List all payment records", description = "Retrieves all processed payment records across the platform")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
}
