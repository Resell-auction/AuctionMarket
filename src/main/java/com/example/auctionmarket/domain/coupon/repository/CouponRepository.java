package com.example.auctionmarket.domain.coupon.repository;

import com.example.auctionmarket.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
