package com.example.auctionmarket.domain.coupon.entity;

import com.example.auctionmarket.common.entity.TimeStamped;
import com.example.auctionmarket.domain.coupon.enums.CouponStatus;
import com.example.auctionmarket.domain.coupon.enums.CouponType;
import com.example.auctionmarket.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name="coupons")
public class Coupon extends TimeStamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String couponName;

    private String description;

    private Long discountAmount;

    private LocalDateTime expiredAt;

    private int amount;

    private CouponStatus couponStatus;

    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    @OneToMany(mappedBy = "coupons", cascade = CascadeType.ALL)
    private List<CouponUser> couponUserList = new ArrayList<>();

    //   private String condition;

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
