package com.example.auctionmarket.domain.productimage.repository;

import com.example.auctionmarket.domain.productimage.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
