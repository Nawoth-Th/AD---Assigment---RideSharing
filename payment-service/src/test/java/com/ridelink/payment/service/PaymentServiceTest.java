package com.ridelink.payment.service;

import com.ridelink.payment.dto.PaymentResponse;
import com.ridelink.payment.dto.ProcessPaymentRequest;
import com.ridelink.payment.dto.ReceiptResponse;
import com.ridelink.payment.exception.PaymentProcessingException;
import com.ridelink.payment.exception.ResourceNotFoundException;
import com.ridelink.payment.model.Payment;
import com.ridelink.payment.model.PaymentMethod;
import com.ridelink.payment.model.PaymentStatus;
import com.ridelink.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Payment samplePayment;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentService, "currency", "LKR");

        samplePayment = Payment.builder()
                .id("pay-101")
                .rideId("ride-001")
                .passengerId("passenger-10")
                .amount(1000.0)
                .currency("LKR")
                .status(PaymentStatus.COMPLETED)
                .paymentMethod(PaymentMethod.SIMULATED_WALLET)
                .receiptNumber("REC-20260930-ABC123")
                .transactionReference("TXN-XYZ9876543")
                .processedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Should successfully process simulated payment and generate receipt")
    void testProcessPaymentSuccess() {
        ProcessPaymentRequest request = ProcessPaymentRequest.builder()
                .rideId("ride-001")
                .passengerId("passenger-10")
                .amount(1000.0)
                .paymentMethod(PaymentMethod.SIMULATED_WALLET)
                .simulateFailure(false)
                .build();

        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> {
            Payment p = i.getArgument(0);
            p.setId("pay-101");
            return p;
        });

        PaymentResponse response = paymentService.processPayment(request);

        assertNotNull(response);
        assertEquals("pay-101", response.getId());
        assertEquals("ride-001", response.getRideId());
        assertEquals(PaymentStatus.COMPLETED, response.getStatus());
        assertNotNull(response.getReceiptNumber());
        assertTrue(response.getReceiptNumber().startsWith("REC-"));
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should throw PaymentProcessingException when simulateFailure is true (Negative Scenario)")
    void testProcessPaymentSimulatedFailure() {
        ProcessPaymentRequest request = ProcessPaymentRequest.builder()
                .rideId("ride-001")
                .passengerId("passenger-10")
                .amount(1000.0)
                .simulateFailure(true)
                .build();

        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        assertThrows(PaymentProcessingException.class, () -> paymentService.processPayment(request));
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should retrieve digital receipt by receipt number")
    void testGetReceiptSuccess() {
        when(paymentRepository.findByReceiptNumber("REC-20260930-ABC123")).thenReturn(Optional.of(samplePayment));

        ReceiptResponse receipt = paymentService.getReceiptByReceiptNumber("REC-20260930-ABC123");

        assertNotNull(receipt);
        assertEquals("REC-20260930-ABC123", receipt.getReceiptNumber());
        assertEquals("pay-101", receipt.getPaymentId());
        assertEquals(1000.0, receipt.getTotalAmount());
        assertEquals(PaymentStatus.COMPLETED, receipt.getPaymentStatus());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid receipt number")
    void testGetReceiptNotFound() {
        when(paymentRepository.findByReceiptNumber("NON_EXISTENT")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.getReceiptByReceiptNumber("NON_EXISTENT"));
    }

    @Test
    @DisplayName("Should retrieve payment by ride ID")
    void testGetPaymentByRideIdSuccess() {
        when(paymentRepository.findByRideId("ride-001")).thenReturn(Optional.of(samplePayment));

        PaymentResponse response = paymentService.getPaymentByRideId("ride-001");

        assertNotNull(response);
        assertEquals("pay-101", response.getId());
        assertEquals("ride-001", response.getRideId());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when payment by ride ID not found")
    void testGetPaymentByRideIdNotFound() {
        when(paymentRepository.findByRideId("unknown-ride")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.getPaymentByRideId("unknown-ride"));
    }

    @Test
    @DisplayName("Should retrieve all payments")
    void testGetAllPayments() {
        when(paymentRepository.findAll()).thenReturn(java.util.List.of(samplePayment));

        java.util.List<PaymentResponse> payments = paymentService.getAllPayments();

        assertNotNull(payments);
        assertEquals(1, payments.size());
        assertEquals("pay-101", payments.get(0).getId());
    }
}
