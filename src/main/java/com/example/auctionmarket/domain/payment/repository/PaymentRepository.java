package com.example.auctionmarket.domain.payment.repository;

import com.example.auctionmarket.domain.payment.entity.Payment;
import com.example.auctionmarket.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByAuctionId(Long auctionId);

    boolean existsByAuctionId(Long auctionId);
}