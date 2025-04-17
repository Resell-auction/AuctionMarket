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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 제품 이미지 업로드
    @Transactional
    public List<ProductImageResponse> uploadProductImages(AuthUser authUser, Long productId, List<MultipartFile> files) throws IOException {
    // file.getBytes()는 IOException 발생 가능이 있으므로 uploadProductImages 메서드에 throws IOException 붙임

        // 유저 검증 로직 (본인 확인)
        User user = userRepository.findById(authUser.getId()).orElseThrow(
                () -> new UserNotFoundException()
        );

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new AuctionException(AuctionErrorCode.PRODUCT_NOT_FOUND)
        );

        // 본인 제품만 이미지 업로드 가능
        if (!authUser.getEmail().equals(product.getUser().getEmail())) {
            throw new ProductException(ProductErrorCode.NOT_MY_PRODUCT);
        }

        List<ProductImageResponse> responseList = new ArrayList<>();

        for (MultipartFile file : files) {
            // 원본 파일명 추출
            String originFileName = file.getOriginalFilename();
            if (!StringUtils.hasText(originFileName)) {
                throw new ProductImageException(ProductImageErrorCode.IMAGE_NAME_NOT_FOUND);
            }

            // 파일 확장자 추출
            String fileExtension = getFileExtension(originFileName);
            if (!isImageFile(fileExtension)) {
                throw new ProductImageException(ProductImageErrorCode.INVALID_IMAGE_TYPE);
            }

            // S3에 저장할 파일명 생성 (UUID + 확장자) <- 중복을 피하기 위해 UUID 사용
            String fileName = UUID.randomUUID() + fileExtension;
            String key = "uploads/images/" + fileName;

            // S3 파일 업로드 요청 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            // 파일 데이터를 RequestBody로 변환
            RequestBody requestBody = RequestBody.fromBytes(file.getBytes()); // MultipartFile로 받은 파일의 내용을 byte배열로

            // S3에 파일 업로드 실행 (requestBody를 S3에 전송)
            s3Client.putObject(putObjectRequest, requestBody);

            // 업로드 성공 시 파일 URL 반환
            String imageUrl = "https://" + bucket + ".s3.amazonaws.com/" + key;

            ProductImage productImage = new ProductImage(product, fileName, originFileName, imageUrl);
            productImageRepository.save(productImage);

            ProductImageResponse response = new ProductImageResponse(
                    productImage.getId(),
                    productImage.getFileName(),
                    productImage.getOriginFileName(),
                    productImage.getImageUrl()
            );

            responseList.add(response);
        }

        return responseList;
    }

    // 파일 확장자 추출 메서드
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.'); // 마지막 점(.) 인덱스를 찾음
        if (lastDotIndex == -1) { // .이 없을 경우 -1을 반환 -> 확장자가 없다고 판단 -> 빈문자열("")반환
            return "";
        }
        return fileName.substring(lastDotIndex); // 마지막 점 위치부터 끝까지 짜름 ex) .jpg
    }

    // 업로드 가능한 이미지 파일 확인 메서드
    private  boolean isImageFile(String fileExtension) {
        String lowerExtension = fileExtension.toLowerCase();
        return  lowerExtension.equals(".jpg") ||
                lowerExtension.equals(".jpeg") ||
                lowerExtension.equals(".png") ||
                lowerExtension.equals(".gif") ||
                lowerExtension.equals(".pdf");
    }


}
