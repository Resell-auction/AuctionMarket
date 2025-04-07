package com.example.auctionmarket.domain.product.controller;

import com.example.auctionmarket.common.response.Response;
import com.example.auctionmarket.domain.product.dto.request.ProductSaveRequest;
import com.example.auctionmarket.domain.product.dto.request.ProductUpdateRequest;
import com.example.auctionmarket.domain.product.dto.response.ProductResponse;
import com.example.auctionmarket.domain.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 제품 등록
    @PostMapping("/v1/products")
    public Response<ProductResponse> createProduct(
            @Valid @RequestBody ProductSaveRequest request
    ) {
        return Response.of(productService.createProduct(request));
    }

    // 제품 단건 조회
    @GetMapping("/v1/products/{productId}")
    public Response<ProductResponse> getProduct(
            @PathVariable Long productId
    ) {
        return Response.of(productService.getProduct(productId));
    }

    // 제품 전체 조회 (페이징)
    @GetMapping("/v1/products")
    public Response<ProductResponse> getAllProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return Response.fromPage(productService.getAllProducts(page, size));
    }

    // 제품 정보 수정
    @PatchMapping("/v1/products/{productId}")
    public Response<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductUpdateRequest request
    ) {
        return Response.of(productService.updateProduct(productId, request));
    }

    // 제품 삭제
    @DeleteMapping("/v1/products/{productId}")
    public Response<String> deleteProduct(
            @PathVariable Long productId
    ) {
        productService.deleteProduct(productId);
        return Response.of("제품이 삭제되었습니다.");
    }
}
