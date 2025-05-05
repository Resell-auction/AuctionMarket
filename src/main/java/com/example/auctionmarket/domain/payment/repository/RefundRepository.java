package com.example.auctionmarket.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auctionmarket.domain.payment.entity.Refund;

public interface RefundRepository extends JpaRepository<Refund, Long> {
}