package com.example.auctionmarket.domain.productimage.controller;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.common.response.Response;
import com.example.auctionmarket.domain.productimage.dto.response.ProductImageResponse;
import com.example.auctionmarket.domain.productimage.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping("v1/products/{productId}/product-images")
    public Response<List<ProductImageResponse>> uploadProductImages (
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long productId,
            @RequestParam("files") List<MultipartFile> files
            ) throws IOException {
        return Response.of(productImageService.uploadProductImages(authUser, productId, files));
    }

//    @DeleteMapping("v1/products/{productId}/product-images/{productimageId}")
//    public Response
}
