package com.example.auctionmarket.domain.coupon.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.auctionmarket.domain.coupon.entity.Coupon;
import com.example.auctionmarket.domain.coupon.enums.CouponStatus;

import jakarta.persistence.LockModeType;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    List<Coupon> findByCouponStatus(CouponStatus couponStatus);

    @Modifying
    @Transactional
    @Query("UPDATE Coupon c SET c.couponStatus = 'EXPIRED' " +
            "WHERE c.expiredAt BETWEEN :from AND :to " +
            "AND c.couponStatus = 'VALID'")
    int expireCouponsBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Coupon c WHERE c.id = :id")
    Optional<Coupon> findByIdWithPessimisticLock(@Param("id") Long id);
}
