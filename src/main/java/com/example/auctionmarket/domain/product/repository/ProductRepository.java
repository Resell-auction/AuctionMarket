package com.example.auctionmarket.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.auctionmarket.domain.product.entity.Product;
import com.example.auctionmarket.domain.product.enums.ProductCategory;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.user.id = :userId")
    Page<Product> findAllMyProduct(
            Pageable pageable,
            @Param("userId") Long userId
    );

    @Query("SELECT p FROM Product p WHERE p.productName LIKE CONCAT('%', :keyword, '%') AND p.category = :category ")
    Page<Product> findProductsBySearch(
            @Param("keyword") String keyword,
            @Param("category") ProductCategory category,
            Pageable pageable
    );
}
