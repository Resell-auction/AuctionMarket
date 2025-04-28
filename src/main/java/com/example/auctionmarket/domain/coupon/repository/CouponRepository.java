package com.example.auctionmarket.domain.coupon.repository;

import com.example.auctionmarket.domain.coupon.entity.Coupon;
import com.example.auctionmarket.domain.coupon.enums.CouponStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findByCouponStatus(CouponStatus couponStatus);

    @Modifying
    @Transactional
    @Query("UPDATE Coupon c SET c.couponStatus = 'EXPIRED' " +
            "WHERE c.expiredAt BETWEEN :from AND :to " +
            "AND c.couponStatus = 'VALID'")
    int expireCouponsBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

}
