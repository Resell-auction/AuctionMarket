package com.example.auctionmarket.domain.product.service;

import com.example.auctionmarket.domain.product.dto.request.ProductSaveRequest;
import com.example.auctionmarket.domain.product.dto.request.ProductUpdateRequest;
import com.example.auctionmarket.domain.product.dto.response.ProductResponse;
import com.example.auctionmarket.domain.product.entity.Product;
import com.example.auctionmarket.domain.product.enums.ProductCategory;
import com.example.auctionmarket.domain.product.enums.SoldStatus;
import com.example.auctionmarket.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    // 제품 등록
    @Transactional
    public ProductResponse createProduct(ProductSaveRequest request) {
        // 유저 검증 로직 (본인 확인)

        ProductCategory category = ProductCategory.of(request.getCategory());
        Product product = new Product(request.getProductName(), request.getProductContent(), category);

        Product savedProduct = productRepository.save(product);

        return ProductResponse.toDto(savedProduct);
    }

    // 제품 단건 조회
    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long productId) {
        // 유저 검증 로직 (본인 확인)

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalStateException("제품이 존재하지 않습니다.")
        );

        return ProductResponse.toDto(product);
    }

    // 제품 전체 조회 (페이징)
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(int page, int size) {
        // 유저 검증 로직 (본인 확인)

        int adjustPage = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(adjustPage, size);
        Page<Product> productPage = productRepository.findAll(pageable);

        List<ProductResponse> dtoList = productPage.getContent().stream()
                .map(ProductResponse::toDto)
                .toList();

        return new PageImpl<>(dtoList, pageable, productPage.getTotalElements());
    }

    // 제품 정보 수정
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductUpdateRequest request) {

        // 유저 검증 로직 (본인 확인)

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalStateException("제품이 존재하지 않습니다.")
        );

        // 제품 판매 상태가 SOLD일 경우 수정 불가 로직
        if (product.getSoldStatus().equals(SoldStatus.SOLD)) {
            throw new IllegalStateException("이미 판매된 상품이라 수정이 불가합니다.");
        }

        ProductCategory category = ProductCategory.of(request.getCategory());
        product.update(request.getProductName(), request.getProductContent(), category);

        return ProductResponse.toDto(product);

    }

    @Transactional
    public void deleteProduct(Long productId) {

        // 유저 검증 로직 (본인 확인)

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new IllegalStateException("제품이 존재하지 않습니다.")
        );

        // 제품 판매 상태가 SOLD일 경우 삭제 불가 로직
        if (product.getSoldStatus().equals(SoldStatus.SOLD)) {
            throw new IllegalStateException("이미 판매된 상품이라 삭제가 불가합니다.");
        }

        productRepository.deleteById(productId);
    }
}
