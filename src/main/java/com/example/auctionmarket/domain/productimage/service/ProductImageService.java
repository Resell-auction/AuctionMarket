package com.example.auctionmarket.domain.productimage.service;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.auction.exception.AuctionErrorCode;
import com.example.auctionmarket.domain.auction.exception.AuctionException;
import com.example.auctionmarket.domain.product.entity.Product;
import com.example.auctionmarket.domain.product.exception.ProductErrorCode;
import com.example.auctionmarket.domain.product.exception.ProductException;
import com.example.auctionmarket.domain.product.repository.ProductRepository;
import com.example.auctionmarket.domain.productimage.dto.response.ProductImageResponse;
import com.example.auctionmarket.domain.productimage.entity.ProductImage;
import com.example.auctionmarket.domain.productimage.exception.ProductImageErrorCode;
import com.example.auctionmarket.domain.productimage.exception.ProductImageException;
import com.example.auctionmarket.domain.productimage.repository.ProductImageRepository;
import com.example.auctionmarket.domain.user.entity.User;
import com.example.auctionmarket.domain.user.exception.UserNotFoundException;
import com.example.auctionmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // 제품 이미지 등록
    @Transactional
    public List<ProductImageResponse> uploadProductImages(AuthUser authUser, Long productId, List<MultipartFile> files) throws IOException {

        // 유저 검증 로직 (본인 확인)
        User user = userRepository.findById(authUser.getId()).orElseThrow(
                () -> new UserNotFoundException()
        );

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new AuctionException(AuctionErrorCode.PRODUCT_NOT_FOUND)
        );

        // 본인 제품만 조회 가능 로직
        if (!authUser.getEmail().equals(product.getUser().getEmail())) {
            throw new ProductException(ProductErrorCode.NOT_MY_PRODUCT);
        }

        List<ProductImageResponse> responseList = new ArrayList<>();
        List<String> supportedFileTypes = List.of("image/jpeg", "image/png", "image/gif", "application/pdf");

        String uploadDir = "D:/img/";

        for (MultipartFile file : files) {
            // MIME 타입 체크 (MIME : 인터넷에서 전송되는 다양한 종류의 데이터를 식별하기 위한 형식)
            if (!supportedFileTypes.contains(file.getContentType())) {
                throw new ProductImageException(ProductImageErrorCode.INVALID_IMAGE_TYPE);
            }

            // 파일명을 UUID로 변경하여 중복 방지
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = UUID.randomUUID() + fileExtension;

            Path savePath = Paths.get(uploadDir, fileName);

            // 이미지 파일을 로컬에 저장
            Files.write(savePath, file.getBytes());

            ProductImage productImage = new ProductImage(product, originalFileName, savePath.toString());
            productImageRepository.save(productImage);

            ProductImageResponse response = new ProductImageResponse(
                    productImage.getId(),
                    savePath.toString(),
                    productImage.getFileName()
            );
            responseList.add(response);

        }
        return responseList;
    }
}
