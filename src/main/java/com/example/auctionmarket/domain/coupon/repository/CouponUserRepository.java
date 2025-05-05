package com.example.auctionmarket.domain.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auctionmarket.domain.coupon.entity.CouponUser;

public interface CouponUserRepository extends JpaRepository<CouponUser, Long> {

}
