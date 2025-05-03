package com.example.auctionmarket.domain.productimage.controller;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.common.response.Response;
import com.example.auctionmarket.domain.productimage.dto.response.ProductImageResponse;
import com.example.auctionmarket.domain.productimage.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    // 제품 이미지 업로드
    @PostMapping(value = "/v1/products/{productId}/product-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<List<ProductImageResponse>> uploadProductImages(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long productId,
            @RequestPart("files") List<MultipartFile> files
    ) throws IOException {
        return Response.of(productImageService.uploadProductImages(authUser, productId, files));
    }

    // 제품 이미지 삭제
    @DeleteMapping("/v1/products/{productId}/product-images/{productImageId}")
    public Response<String> deleteImage(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long productId,
            @PathVariable Long productImageId
    ) {
        productImageService.deleteImage(authUser, productId, productImageId);

        return Response.of("제품 이미지가 삭제되었습니다.");
    }
}
