package com.example.auctionmarket.domain.coupon.entity;

import com.example.auctionmarket.common.entity.BaseEntity;
import com.example.auctionmarket.domain.coupon.enums.CouponType;
import com.example.auctionmarket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@NoArgsConstructor
@Table(name="couponusers")
@Builder
@AllArgsConstructor
public class CouponUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "user_id", nullable = true)
    private User users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = true)
    private Coupon coupons;

    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    private boolean used = false;

    public void markAsUsed() {
        this.used = true;
    }

    public void markAsUnused() {
        this.used = false;
    }
}
