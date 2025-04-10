package com.example.auctionmarket.domain.product.repository;

import com.example.auctionmarket.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository <Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.user.id = :userId")
    Page<Product> findAllMyProduct(
            Pageable pageable,
            @Param("userId") Long userId
    );

}
