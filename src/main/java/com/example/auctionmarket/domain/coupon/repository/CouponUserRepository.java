package com.example.auctionmarket.domain.coupon.repository;

import com.example.auctionmarket.domain.coupon.entity.CouponUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponUserRepository extends JpaRepository<CouponUser, Long> {
}
