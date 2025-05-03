package com.example.auctionmarket.domain.coupon.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.auctionmarket.common.entity.BaseEntity;
import com.example.auctionmarket.domain.coupon.enums.CouponStatus;
import com.example.auctionmarket.domain.coupon.enums.CouponType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name="coupons")
public class Coupon extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String couponName;

    private String description;

    private Long discountAmount;

    private LocalDateTime expiredAt;

    private int amount;

    @Enumerated(EnumType.STRING)
    private CouponStatus couponStatus;

    @Version
    private Long version; // 낙관적 락을 위한 필드

    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    @OneToMany(mappedBy = "coupons", cascade = CascadeType.ALL)
    private List<CouponUser> couponUserList = new ArrayList<>();

    public Coupon(String couponName, String description, Long discountAmount, LocalDateTime expiredAt, int amount, CouponType couponType) {
        this.couponName=couponName;
        this.description=description;
        this.discountAmount=discountAmount;
        this.expiredAt=expiredAt;
        this.amount=amount;
        this.couponStatus=CouponStatus.VALID;
        this.couponType=couponType;
    }

    public void update(String couponName, String description, Long discountAmount, LocalDateTime expiredAt){
        this.couponName=couponName;
        this.description=description;
        this.discountAmount=discountAmount;
        this.expiredAt=expiredAt;
    }

    public void expiredCoupon(){
        this.couponStatus=CouponStatus.EXPIRED;
    }

    public void setUsers(CouponUser couponUser){
        couponUserList.add(couponUser);
    }

    public void assignUniqueCoupon(){
        if (this.amount <= 0) {
            throw new IllegalStateException("쿠폰이 더 이상 남아있지 않습니다.");
        }
        this.amount--;
    }

    public void discountCoupon(int amount){
        this.amount= this.amount-amount;
    }

    public Long calculateDiscountRate(Long paymentAmount) {
        if (this.couponType == CouponType.FIXED) {
            return discountAmount;
        } else if (this.couponType == CouponType.PERCENT) {
            return paymentAmount * this.discountAmount / 100;
        } else {
            throw new IllegalArgumentException("CouponType not supported");
        }
    }
}
