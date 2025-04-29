package com.example.auctionmarket.domain.coupon.repository;

import com.example.auctionmarket.domain.coupon.entity.Coupon;
import com.example.auctionmarket.domain.coupon.entity.CouponUser;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponUserRepository extends JpaRepository<CouponUser, Long> {

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("SELECT c FROM Coupon c WHERE c.id = :id")
//    Optional<Coupon> findByIdWithPessimisticLock(@Param("id") Long id);

}
