package com.example.auctionmarket.domain.product.service;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.auction.exception.AuctionErrorCode;
import com.example.auctionmarket.domain.auction.exception.AuctionException;
import com.example.auctionmarket.domain.product.dto.request.ProductSaveRequest;
import com.example.auctionmarket.domain.product.dto.request.ProductUpdateRequest;
import com.example.auctionmarket.domain.product.dto.response.ProductResponse;
import com.example.auctionmarket.domain.product.entity.Product;
import com.example.auctionmarket.domain.product.enums.ProductCategory;
import com.example.auctionmarket.domain.product.enums.SoldStatus;
import com.example.auctionmarket.domain.product.exception.ProductErrorCode;
import com.example.auctionmarket.domain.product.exception.ProductException;
import com.example.auctionmarket.domain.product.repository.ProductRepository;
import com.example.auctionmarket.domain.user.entity.User;
import com.example.auctionmarket.domain.user.exception.UserNotFoundException;
import com.example.auctionmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // 제품 등록
    @Transactional
    public ProductResponse createProduct(AuthUser authUser, ProductSaveRequest request) {

        // 유저 검증 로직 (본인 확인)
        User user = userRepository.findByEmail(authUser.getEmail()).orElseThrow(
                () -> new UserNotFoundException()
        );

        ProductCategory category = ProductCategory.of(request.getCategory());
        Product product = new Product(user, request.getProductName(), request.getProductContent(), category);

        Product savedProduct = productRepository.save(product);

        return ProductResponse.toDto(savedProduct);
    }

    // 제품 단건 조회
    @Transactional(readOnly = true)
    public ProductResponse getProduct(AuthUser authUser, Long productId) {

        // 유저 검증 로직 (본인 확인)
        User user = userRepository.findByEmail(authUser.getEmail()).orElseThrow(
                () -> new UserNotFoundException()
        );

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new AuctionException(AuctionErrorCode.PRODUCT_NOT_FOUND)
        );

        // 본인 제품만 조회 가능 로직
        if (!authUser.getEmail().equals(user.getEmail())) {
            throw new ProductException(ProductErrorCode.NOT_MY_PRODUCT);
        }

        return ProductResponse.toDto(product);
    }

    // 제품 전체 조회 (페이징)
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(AuthUser authUser, int page, int size) {

        // 유저 검증 로직 (본인 확인)
        User user = userRepository.findByEmail(authUser.getEmail()).orElseThrow(
                () -> new UserNotFoundException()
        );

        int adjustPage = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(adjustPage, size);
        Page<Product> productPage = productRepository.findAllMyProduct(pageable, user.getId());

        List<ProductResponse> dtoList = productPage.getContent().stream()
                .map(ProductResponse::toDto)
                .toList();

        return new PageImpl<>(dtoList, pageable, productPage.getTotalElements());
    }

    // 제품 정보 수정
    @Transactional
    public ProductResponse updateProduct(AuthUser authUser, Long productId, ProductUpdateRequest request) {

        // 유저 검증 로직 (본인 확인)
        User user = userRepository.findByEmail(authUser.getEmail()).orElseThrow(
                () -> new UserNotFoundException()
        );

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new AuctionException(AuctionErrorCode.PRODUCT_NOT_FOUND)
        );

        // 제품 판매 상태가 SOLD일 경우 수정 불가 로직
        if (product.getSoldStatus().equals(SoldStatus.SOLD)) {
            throw new ProductException(ProductErrorCode.PRODUCT_ALREADY_SOLD);
        }

        // 본인 제품만 수정 가능 로직
        if (!authUser.getEmail().equals(user.getEmail())) {
            throw new ProductException(ProductErrorCode.NOT_MY_PRODUCT);
        }

        ProductCategory category = ProductCategory.of(request.getCategory());
        product.update(request.getProductName(), request.getProductContent(), category);

        return ProductResponse.toDto(product);

    }

    @Transactional
    public void deleteProduct(AuthUser authUser, Long productId) {

        // 유저 검증 로직 (본인 확인)
        User user = userRepository.findByEmail(authUser.getEmail()).orElseThrow(
                () -> new UserNotFoundException()
        );

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new AuctionException(AuctionErrorCode.PRODUCT_NOT_FOUND)
        );

        // 제품 판매 상태가 SOLD일 경우 삭제 불가 로직
        if (product.getSoldStatus().equals(SoldStatus.SOLD)) {
            throw new ProductException(ProductErrorCode.PRODUCT_ALREADY_SOLD);
        }

        // 본인 제품만 삭제 가능 로직
        if (!authUser.getEmail().equals(user.getEmail())) {
            throw new ProductException(ProductErrorCode.NOT_MY_PRODUCT);
        }

        productRepository.deleteById(productId);
    }

    public void changeSoldStatus(Long productId) {}
}
