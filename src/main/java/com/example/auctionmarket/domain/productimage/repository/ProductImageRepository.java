package com.example.auctionmarket.domain.productimage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auctionmarket.domain.productimage.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}
