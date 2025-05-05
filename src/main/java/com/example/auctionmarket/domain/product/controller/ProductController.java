package com.example.auctionmarket.domain.product.controller;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.common.response.Response;
import com.example.auctionmarket.domain.product.dto.request.ProductSaveRequest;
import com.example.auctionmarket.domain.product.dto.request.ProductUpdateRequest;
import com.example.auctionmarket.domain.product.dto.response.ProductResponse;
import com.example.auctionmarket.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 제품 등록
    @PostMapping("/v1/products")
    public Response<ProductResponse> createProduct(
            @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody ProductSaveRequest request
    ) {
        return Response.of(productService.createProduct(authUser, request));
    }

    // 제품 단건 조회
    @GetMapping("/v1/products/{productId}")
    public Response<ProductResponse> getProduct(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long productId
    ) {
        return Response.of(productService.getProduct(authUser, productId));
    }

    // 제품 전체 조회 (페이징)
    @GetMapping("/v1/products")
    public Response<ProductResponse> getAllProducts(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return Response.fromPage(productService.getAllProducts(authUser, page, size));
    }

    // 제품 검색
    @GetMapping("/v1/products/search")
    public Response<ProductResponse> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal AuthUser authUser
    ) {
        return Response.fromPage(productService.searchProducts(keyword, category, page, size, authUser));
    }

    // 제품 정보 수정
    @PatchMapping("/v1/products/{productId}")
    public Response<ProductResponse> updateProduct(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long productId,
            @RequestBody ProductUpdateRequest request
    ) {
        return Response.of(productService.updateProduct(authUser, productId, request));
    }

    // 제품 삭제
    @DeleteMapping("/v1/products/{productId}")
    public Response<String> deleteProduct(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long productId
    ) {
        productService.deleteProduct(authUser, productId);
        return Response.of("제품이 삭제되었습니다.");
    }
}
