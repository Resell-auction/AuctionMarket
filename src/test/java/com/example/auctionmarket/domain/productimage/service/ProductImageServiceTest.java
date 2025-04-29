package com.example.auctionmarket.domain.productimage.service;

import com.example.auctionmarket.common.auth.AuthUser;
import com.example.auctionmarket.domain.product.entity.Product;
import com.example.auctionmarket.domain.product.enums.ProductCategory;
import com.example.auctionmarket.domain.product.repository.ProductRepository;
import com.example.auctionmarket.domain.productimage.dto.response.ProductImageResponse;
import com.example.auctionmarket.domain.productimage.entity.ProductImage;
import com.example.auctionmarket.domain.productimage.repository.ProductImageRepository;
import com.example.auctionmarket.domain.user.entity.User;
import com.example.auctionmarket.domain.user.enums.Role;
import com.example.auctionmarket.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductImageServiceTest {

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3Client s3Client;

    @Mock
    private MultipartFile multipartFile;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.cloudfront.domain}")
    private String cloudFrontDomain;

    @InjectMocks
    private ProductImageService productImageService;

    @BeforeEach
    public void setup() {
        // 리플렉션을 이용해 @Value 필드 주입
        ReflectionTestUtils.setField(productImageService, "bucket", "test-bucket");
        ReflectionTestUtils.setField(productImageService, "cloudFrontDomain", "https://cdn.example.com");
    }

    @Test
    public void uploadProductImages_product를_정상적으로_업로드한다() throws IOException {
        // given
        Long productId = 1L;
        String originFileName = "image.jpg";
        byte[] fileContent = "test image".getBytes();

        AuthUser authUser = new AuthUser(1L, "aaa@aaa.com", Role.USER, "닉네임");
        User user = new User("aaa@aaa.com", "password","닉네임", "010-1111-1111", Role.USER);
        ReflectionTestUtils.setField(user, "id", authUser.getId());

        Product product = new Product(user, "아디다스 신발", "아디다스 신발 한정판", ProductCategory.SHOES);
        ReflectionTestUtils.setField(product, "id", productId);

        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

        given(multipartFile.getOriginalFilename()).willReturn(originFileName);
        given(multipartFile.getContentType()).willReturn("image/jpeg");
        given(multipartFile.getBytes()).willReturn(fileContent);

        List<MultipartFile> files = List.of(multipartFile);

        ProductImage productImage = new ProductImage(product, "uuid.jpg", originFileName,  "https://test-bucket.s3.amazonaws.com/uploads/images/uuid.jpg",
                "https://cdn.example.com/uploads/images/uuid.jpg");
        ReflectionTestUtils.setField(productImage, "id", 1L);

        given(productImageRepository.save(any())).willReturn(productImage);

        // when
        List<ProductImageResponse> result = productImageService.uploadProductImages(authUser, product.getId(), files);

        // then
        ProductImageResponse response = result.get(0);
        assertEquals(1, result.size());
        assertThat(response.getFileName()).endsWith(".jpg");
        assertThat(response.getOriginFileName()).isEqualTo(originFileName);
        assertThat(response.getS3ImageUrl()).contains("test-bucket.s3.amazonaws.com");
        assertThat(response.getCloudFrontImageUrl()).contains("https://cdn.example.com/uploads/images/");

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(productImageRepository, times(1)).save(any(ProductImage.class));
    }

    @Test
    void deleteImage_정상적으로_이미지를_삭제한다() {
        // given
        Long productId = 1L;
        Long imageId = 1L;
        String fileName = "test-image.jpg";
        String key = "uploads/images/" + fileName;

        AuthUser authUser = new AuthUser(1L, "aaa@aaa.com", Role.USER, "닉네임");
        User user = new User("aaa@aaa.com", "password","닉네임", "010-1111-1111", Role.USER);
        ReflectionTestUtils.setField(user, "id", authUser.getId());

        Product product = new Product(user, "아디다스 신발", "아디다스 신발 한정판", ProductCategory.SHOES);
        ReflectionTestUtils.setField(product, "id", productId);

        ProductImage productImage = new ProductImage(product, fileName, "original.jpg", "s3-url", "cloudfront-url");
        ReflectionTestUtils.setField(productImage, "id", imageId);

        given(userRepository.findById(authUser.getId())).willReturn(Optional.of(user));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(productImageRepository.findById(imageId)).willReturn(Optional.of(productImage));

        // when
        productImageService.deleteImage(authUser, productId, imageId);

        // then
        verify(productImageRepository, times(1)).delete(productImage);
    }


}