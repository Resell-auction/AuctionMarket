package com.example.auctionmarket.domain.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auctionmarket.domain.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByAuctionId(Long auctionId);

    boolean existsByAuctionId(Long auctionId);
}