package com.example.auctionmarket.domain.payment.repository;

import com.example.auctionmarket.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
