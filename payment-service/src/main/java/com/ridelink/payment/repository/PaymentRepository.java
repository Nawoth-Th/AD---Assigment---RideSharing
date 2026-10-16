package com.ridelink.payment.repository;

import com.ridelink.payment.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    Optional<Payment> findByRideId(String rideId);
    Optional<Payment> findByReceiptNumber(String receiptNumber);
    List<Payment> findByPassengerId(String passengerId);
}
