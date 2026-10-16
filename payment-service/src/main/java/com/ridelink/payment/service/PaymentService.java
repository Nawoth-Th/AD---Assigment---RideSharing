package com.ridelink.payment.service;

import com.ridelink.payment.dto.PaymentResponse;
import com.ridelink.payment.dto.ProcessPaymentRequest;
import com.ridelink.payment.dto.ReceiptResponse;
import com.ridelink.payment.exception.PaymentProcessingException;
import com.ridelink.payment.exception.ResourceNotFoundException;
import com.ridelink.payment.model.Payment;
import com.ridelink.payment.model.PaymentStatus;
import com.ridelink.payment.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Value("${app.fare.currency:LKR}")
    private String currency;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern("yyyyMMdd")
            .withZone(ZoneId.systemDefault());

    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        log.info("Processing simulated payment for ride: {}, amount: {}", request.getRideId(), request.getAmount());

        // Negative scenario demonstration: simulated failure trigger
        if (request.isSimulateFailure()) {
            Payment failedPayment = Payment.builder()
                    .rideId(request.getRideId())
                    .passengerId(request.getPassengerId())
                    .amount(request.getAmount())
                    .currency(currency)
                    .status(PaymentStatus.FAILED)
                    .paymentMethod(request.getPaymentMethod())
                    .failureReason("Simulated card decline / insufficient wallet balance")
                    .processedAt(Instant.now())
                    .createdAt(Instant.now())
                    .build();

            paymentRepository.save(failedPayment);
            log.warn("Payment simulation failed deliberately for testing: {}", request.getRideId());
            throw new PaymentProcessingException("Simulated payment failed: Card declined or insufficient funds.");
        }

        String receiptNumber = "REC-" + DATE_FORMATTER.format(Instant.now()) + "-" +
                UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String transactionRef = "TXN-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();

        Payment payment = Payment.builder()
                .rideId(request.getRideId())
                .passengerId(request.getPassengerId())
                .amount(request.getAmount())
                .currency(currency)
                .status(PaymentStatus.COMPLETED)
                .paymentMethod(request.getPaymentMethod())
                .receiptNumber(receiptNumber)
                .transactionReference(transactionRef)
                .processedAt(Instant.now())
                .createdAt(Instant.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment processed successfully. Receipt: {}", receiptNumber);

        return mapToResponse(savedPayment);
    }

    public PaymentResponse getPaymentById(String id) {
        log.info("Fetching payment by ID: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));
        return mapToResponse(payment);
    }

    public PaymentResponse getPaymentByRideId(String rideId) {
        log.info("Fetching payment for ride ID: {}", rideId);
        Payment payment = paymentRepository.findByRideId(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("No payment record found for ride ID: " + rideId));
        return mapToResponse(payment);
    }

    public ReceiptResponse getReceiptByReceiptNumber(String receiptNumber) {
        log.info("Fetching receipt: {}", receiptNumber);
        Payment payment = paymentRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found: " + receiptNumber));

        return ReceiptResponse.builder()
                .receiptNumber(payment.getReceiptNumber())
                .paymentId(payment.getId())
                .rideId(payment.getRideId())
                .passengerId(payment.getPassengerId())
                .totalAmount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getStatus())
                .transactionReference(payment.getTransactionReference())
                .issuedAt(payment.getProcessedAt())
                .notes("Official digital receipt generated by RideLink Fare & Payment Service.")
                .build();
    }

    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .rideId(payment.getRideId())
                .passengerId(payment.getPassengerId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .receiptNumber(payment.getReceiptNumber())
                .transactionReference(payment.getTransactionReference())
                .failureReason(payment.getFailureReason())
                .processedAt(payment.getProcessedAt())
                .build();
    }
}
