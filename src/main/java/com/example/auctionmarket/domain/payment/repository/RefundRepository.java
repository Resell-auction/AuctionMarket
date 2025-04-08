package com.example.auctionmarket.domain.payment.repository;

import com.example.auctionmarket.domain.payment.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundRepository extends JpaRepository<Refund, Long> {
}
