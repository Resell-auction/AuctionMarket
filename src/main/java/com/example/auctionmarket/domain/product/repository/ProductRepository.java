package com.example.auctionmarket.domain.product.repository;

import com.example.auctionmarket.domain.product.entity.Product;
import com.example.auctionmarket.domain.product.enums.ProductCategory;
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

    @Query("SELECT p FROM Product p WHERE p.productName LIKE CONCAT('%', :keyword, '%') AND p.category = :category ")
//    @Query(value = "SELECT * FROM (SELECT * FROM product WHERE category = :category ORDER BY product_name) AS sub WHERE product_name LIKE CONCAT('%', :keyword, '%') ", nativeQuery = true,
//    countQuery = "SELECT COUNT(*) FROM (SELECT * FROM product WHERE category = :category) AS sub WHERE product_name LIKE CONCAT('%', :keyword, '%') ")
//    @Query(value = "SELECT * FROM (SELECT * FROM product WHERE category = :category) AS sub WHERE product_name LIKE CONCAT('%', :keyword, '%') ", nativeQuery = true,
//            countQuery = "SELECT COUNT(*) FROM (SELECT * FROM product WHERE category = :category) AS sub WHERE product_name LIKE CONCAT('%', :keyword, '%') ")
//    @Query(value = "SELECT * FROM product WHERE MATCH(product_name) AGAINST(:keyword IN BOOLEAN MODE) AND category = :category", nativeQuery = true)
//    @Query(value = "SELECT * FROM (SELECT * FROM product WHERE category = :category) WHERE MATCH(product_name) AGAINST(:keyword IN BOOLEAN MODE) ", nativeQuery = true)
    Page<Product> findProductsBySearch(
            @Param("keyword") String keyword,
//            @Param("category") String category,
            @Param("category") ProductCategory category,
            Pageable pageable
    );
}
