package com.example.auctionmarket.domain.productimage.entity;

import com.example.auctionmarket.common.entity.BaseEntity;
import com.example.auctionmarket.domain.product.entity.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "product_images")
public class ProductImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 저장된 파일 이름 (UUID + 이미지 이름) <- 이미지 이름 중복 방지를 위해 ex) image12514.jpg
    @Column(name = "file_name")
    private String fileName;

    // 원본 파일 이름 ex) Spring.jpg
    @Column(name = "origin_file_name")
    private String originFileName;

    // 저장한 이미지 경로
    @Column(name = "s3_image_url")
    private String s3ImageUrl;

    @Column(name = "cloud_front_image_url")
    private String cloudFrontImageUrl;

    public ProductImage(Product product, String fileName, String originFileName, String s3ImageUrl, String cloudFrontImageUrl) {
        this.product = product;
        this.fileName = fileName;
        this.originFileName = originFileName;
        this.s3ImageUrl = s3ImageUrl;
        this.cloudFrontImageUrl = cloudFrontImageUrl;
    }
}
