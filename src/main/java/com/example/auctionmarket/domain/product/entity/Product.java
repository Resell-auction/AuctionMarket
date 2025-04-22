package com.example.auctionmarket.domain.product.entity;

import com.example.auctionmarket.common.entity.TimeStamped;
import com.example.auctionmarket.domain.product.enums.ProductCategory;
import com.example.auctionmarket.domain.product.enums.SoldStatus;
import com.example.auctionmarket.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "product")
public class Product extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "auction_id")
    private Long auctionId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_content")
    private String productContent;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "sold_status")
    private SoldStatus soldStatus;

    public Product(User user, String productName, String productContent, ProductCategory category) {
        this.user = user;
        this.productName = productName;
        this.productContent = productContent;
        this.category = category;
        this.soldStatus = SoldStatus.UNSOLD;
    }

    public void update(String productName, String productContent, ProductCategory category) {
        this.productName = productName;
        this.productContent = productContent;
        this.category = category;
    }

    public void updateSoldStatus(SoldStatus soldStatus) {
        this.soldStatus = soldStatus;
    }
}

