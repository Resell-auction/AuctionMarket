package com.example.auctionmarket.domain.productimage.entity;

import com.example.auctionmarket.common.entity.TimeStamped;
import com.example.auctionmarket.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity(name = "product_image")
public class ProductImage extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "file_name")
    private String fileName;

    // 저장한 이미지 경로 (UUID + 이미지 이름) <- 이미지 이름 중복 방지를 위해
    @Column(name = "image_path")
    private String imagePath;

    public ProductImage(Product product, String fileName, String imagePath) {
        this.product = product;
        this.fileName = fileName;
        this.imagePath = imagePath;
    }
}
